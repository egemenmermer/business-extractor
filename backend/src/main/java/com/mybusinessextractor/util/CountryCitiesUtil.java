package com.mybusinessextractor.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that provides lists of cities for different countries.
 * Used for breaking down country-wide searches into city-specific searches.
 */
@Component
public class CountryCitiesUtil {

    private final Map<String, List<String>> countryCitiesMap = new HashMap<>();

    public CountryCitiesUtil() {
        initializeCities();
    }

    private void initializeCities() {
        // Turkish cities (81 provinces)
        List<String> turkishCities = Arrays.asList(
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", 
            "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", 
            "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", 
            "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin", 
            "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli", 
            "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", 
            "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat", 
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", 
            "Karaman", "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", 
            "Kilis", "Osmaniye", "Düzce"
        );
        countryCitiesMap.put("Turkey", turkishCities);
        countryCitiesMap.put("turkey", turkishCities); // lowercase version for case-insensitive lookup
        
        // Add more countries as needed
        // countryCitiesMap.put("United States", usCities);
        // countryCitiesMap.put("Germany", germanCities);
    }

    /**
     * Check if the location is a country that we have city data for
     * @param location The location to check
     * @return true if the location is a country with city data
     */
    public boolean isCountry(String location) {
        return countryCitiesMap.containsKey(location);
    }

    /**
     * Get the list of cities for a given country
     * @param country The country name
     * @return List of cities, or empty list if the country is not found
     */
    public List<String> getCitiesForCountry(String country) {
        return countryCitiesMap.getOrDefault(country, List.of());
    }
} 