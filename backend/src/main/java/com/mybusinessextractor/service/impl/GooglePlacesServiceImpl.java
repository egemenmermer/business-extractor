package com.mybusinessextractor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.service.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Google Places API service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlacesServiceImpl implements GooglePlacesService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${google.places.api.key}")
    private String apiKey;

    @Value("${google.places.api.base-url}")
    private String baseUrl;

    private static final Map<String, String> CATEGORY_TRANSLATIONS = Map.of(
        "Diş Hekimliği", "dental clinic",
        "Diş", "dental",
        "Dişçi", "dentist",
        "Hastane", "hospital",
        "Restoran", "restaurant",
        "Kafe", "cafe", 
        "Berber", "barber",
        "Kuaför", "hairdresser",
        "Avukat", "lawyer"
    );

    private static final Map<String, String> LOCATION_TRANSLATIONS = Map.of(
        "Türkiye", "Turkey",
        "İstanbul", "Istanbul",
        "Ankara", "Ankara",
        "İzmir", "Izmir"
    );

    /**
     * Searches for businesses using the Google Places Text Search API.
     *
     * @param category The business category to search for
     * @param location The location to search within
     * @return A Flux of Business objects
     */
    @Override
    public Flux<Business> searchBusinesses(String category, String location) {
        // Translate Turkish categories and locations to English if translations exist
        String searchCategory = CATEGORY_TRANSLATIONS.getOrDefault(category, category);
        String searchLocation = LOCATION_TRANSLATIONS.getOrDefault(location, location);
        
        log.info("Searching for '{}' in '{}' (translated from: '{}' in '{}')", 
                searchCategory, searchLocation, category, location);
        
        // Use a more specific query format that combines the category and location in different ways
        // This approach yields better and more comprehensive results across different locations
        String query = String.format("%s in %s", searchCategory, searchLocation);
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        
        // Use a more region-targeted approach for detailed results
        return fetchPlacesPage(encodedQuery, null);
    }

    /**
     * Helper method to fetch places with pagination support
     * 
     * @param query The encoded query string
     * @param pageToken The next page token, or null for the first page
     * @return A Flux of Business objects
     */
    private Flux<Business> fetchPlacesPage(String query, String pageToken) {
        String url;
        if (pageToken != null) {
            url = String.format("%s/textsearch/json?pagetoken=%s&key=%s", baseUrl, pageToken, apiKey);
            log.info("Fetching next page of results using token");
        } else {
            url = String.format("%s/textsearch/json?query=%s&key=%s", baseUrl, query, apiKey);
            log.info("Making initial API request: {}", url.replace(apiKey, "API_KEY_HIDDEN"));
        }
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMapMany(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        String status = root.path("status").asText();
                        
                        if ("REQUEST_DENIED".equals(status)) {
                            String errorMessage = root.path("error_message").asText("Unknown error");
                            log.error("Google Places API request denied: {}", errorMessage);
                            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, 
                                    "Google Places API request denied: " + errorMessage);
                        }
                        
                        if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
                            log.error("Google Places API error: {}", status);
                            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, 
                                    "Google Places API error: " + status);
                        }
                        
                        JsonNode results = root.path("results");
                        if (results.size() == 0) {
                            log.warn("No results found for this page");
                            return Flux.empty();
                        } else {
                            log.info("Found {} results on this page", results.size());
                        }
                        
                        // Check for next page token
                        String nextPageToken = root.path("next_page_token").asText(null);
                        
                        Flux<Business> currentPageFlux = Flux.fromIterable(results)
                                .map(this::mapToBasicBusiness);
                        
                        // If we have a next page token, recursively fetch the next page after a delay
                        // (Google requires a short delay before using the next_page_token)
                        if (nextPageToken != null && !nextPageToken.isEmpty()) {
                            log.info("Next page token found, will fetch next page after delay");
                            return currentPageFlux.concatWith(
                                    Mono.delay(Duration.ofSeconds(2))
                                    .flatMapMany(ignored -> fetchPlacesPage(query, nextPageToken))
                            );
                        }
                        
                        return currentPageFlux;
                    } catch (IOException e) {
                        log.error("Error parsing Google Places API response", e);
                        return Flux.error(e);
                    }
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .filter(ex -> !(ex instanceof ResponseStatusException))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doAfterRetry(retrySignal -> 
                            log.warn("Retrying API request after failure. Attempt: {}/3", 
                                    retrySignal.totalRetries() + 1))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            log.error("All retry attempts failed");
                            return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                                    "Failed to retrieve data after multiple attempts");
                        }));
    }

    /**
     * Fetches detailed information for a specific place using Place Details API.
     *
     * @param placeId The Google Place ID
     * @return A Business object with detailed information
     */
    @Override
    public Business getBusinessDetails(String placeId) {
        String url = String.format("%s/details/json?place_id=%s&fields=name,formatted_address,formatted_phone_number," +
                "website,address_component,geometry,url,international_phone_number&key=%s", baseUrl, placeId, apiKey);

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            JsonNode root = objectMapper.readTree(response);
            String status = root.path("status").asText();
            
            if (!"OK".equals(status)) {
                log.error("Google Places API error: {}", status);
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, 
                        "Google Places API error: " + status);
            }
            
            JsonNode result = root.path("result");
            return mapToDetailedBusiness(result);
        } catch (IOException e) {
            log.error("Error parsing Google Places API response", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error processing place details", e);
        }
    }

    /**
     * Extracts email from a website by crawling (if possible).
     *
     * @param website The website URL
     * @return The extracted email or null if not found
     */
    @Override
    public String extractEmail(String website) {
        if (website == null || website.isEmpty()) {
            return null;
        }

        String sanitizedWebsite = website;
        // Ensure website has proper protocol
        if (!website.startsWith("http://") && !website.startsWith("https://")) {
            sanitizedWebsite = "https://" + website;
        }

        log.info("Attempting to extract email from website: {}", sanitizedWebsite);
        
        try {
            // Set timeout and user agent to appear more like a browser request
            String response = webClient.get()
                    .uri(sanitizedWebsite)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(15))
                    .block(Duration.ofSeconds(20));

            if (response != null) {
                // Pattern for matching email addresses (improved regex)
                Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
                Matcher matcher = pattern.matcher(response);
                
                // Find all matches and return the first valid one
                while (matcher.find()) {
                    String potentialEmail = matcher.group();
                    // Basic validation - check for common disposable domains or suspicious patterns
                    if (!potentialEmail.contains("example.com") && 
                        !potentialEmail.contains("domain.com") && 
                        !potentialEmail.contains("email.com") &&
                        potentialEmail.length() < 100) { // Simple length check to avoid malformed matches
                        
                        log.info("Successfully extracted email: {} from {}", potentialEmail, sanitizedWebsite);
                        return potentialEmail;
                    }
                }
                
                // If no emails found in the main page, try to check contact page if it exists
                if (response.contains("contact") || response.contains("Contact") || response.contains("CONTACT")) {
                    // Try to find contact page links
                    Pattern contactPattern = Pattern.compile("href=[\"'](/contact[^\"']*|/about[^\"']*|/kontakt[^\"']*)[\"']", 
                            Pattern.CASE_INSENSITIVE);
                    Matcher contactMatcher = contactPattern.matcher(response);
                    
                    if (contactMatcher.find()) {
                        String contactPath = contactMatcher.group(1);
                        String contactUrl = sanitizedWebsite;
                        if (contactUrl.endsWith("/")) {
                            contactUrl = contactUrl.substring(0, contactUrl.length() - 1);
                        }
                        contactUrl += contactPath;
                        
                        log.info("Checking contact page for email: {}", contactUrl);
                        return extractEmailFromContactPage(contactUrl);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract email from website: {}", sanitizedWebsite, e);
        }
        
        return null;
    }

    /**
     * Helper method to extract email from a contact page.
     * 
     * @param contactUrl The URL of the contact page
     * @return The extracted email or null if not found
     */
    private String extractEmailFromContactPage(String contactUrl) {
        try {
            String response = webClient.get()
                    .uri(contactUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block(Duration.ofSeconds(15));
                    
            if (response != null) {
                Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
                Matcher matcher = pattern.matcher(response);
                
                if (matcher.find()) {
                    String email = matcher.group();
                    log.info("Found email on contact page: {}", email);
                    return email;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract email from contact page: {}", contactUrl, e);
        }
        
        return null;
    }

    /**
     * Maps a JSON node from Places API to a basic Business object.
     */
    private Business mapToBasicBusiness(JsonNode node) {
        String placeId = node.path("place_id").asText();
        String name = node.path("name").asText();
        String vicinity = node.path("vicinity").asText();
        JsonNode location = node.path("geometry").path("location");
        Double lat = location.path("lat").asDouble();
        Double lng = location.path("lng").asDouble();
        
        return Business.builder()
                .id(placeId)
                .businessName(name)
                .address(vicinity)
                .latitude(lat)
                .longitude(lng)
                .mapsLink(String.format("https://www.google.com/maps/place/?q=place_id:%s", placeId))
                .build();
    }

    /**
     * Maps a JSON node from Place Details API to a detailed Business object.
     */
    private Business mapToDetailedBusiness(JsonNode node) {
        String placeId = node.path("place_id").asText();
        String name = node.path("name").asText();
        String address = node.path("formatted_address").asText();
        String phone = node.path("formatted_phone_number").asText();
        String website = node.path("website").asText(null);
        
        if (website == null || website.isEmpty()) {
            website = node.path("url").asText(null);
        }
        
        JsonNode location = node.path("geometry").path("location");
        Double lat = location.path("lat").asDouble();
        Double lng = location.path("lng").asDouble();

        // Extract address components
        String city = "";
        String state = "";
        String postalCode = "";
        String country = "";
        
        JsonNode addressComponents = node.path("address_components");
        for (JsonNode component : addressComponents) {
            JsonNode types = component.path("types");
            String longName = component.path("long_name").asText();
            
            for (JsonNode type : types) {
                String typeValue = type.asText();
                
                switch (typeValue) {
                    case "locality":
                        city = longName;
                        break;
                    case "administrative_area_level_1":
                        state = longName;
                        break;
                    case "postal_code":
                        postalCode = longName;
                        break;
                    case "country":
                        country = longName;
                        break;
                }
            }
        }

        // Extract email from website if possible and website is not null
        String email = website != null ? extractEmail(website) : null;

        log.info("Found website: {} for business: {}", website, name);
        if (email != null) {
            log.info("Extracted email: {} from website", email);
        }

        return Business.builder()
                .id(placeId)
                .businessName(name)
                .address(address)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country(country)
                .phone(phone)
                .email(email)
                .website(website)
                .latitude(lat)
                .longitude(lng)
                .mapsLink(String.format("https://www.google.com/maps/place/?q=place_id:%s", placeId))
                .build();
    }
} 