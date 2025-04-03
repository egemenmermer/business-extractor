package com.mybusinessextractor.service;

import com.mybusinessextractor.model.Business;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Service interface for interacting with the Google Places API.
 */
public interface GooglePlacesService {
    
    /**
     * Searches for businesses using the Google Places Text Search API.
     *
     * @param category The business category to search for
     * @param location The location to search within
     * @return A Flux of Business objects
     */
    Flux<Business> searchBusinesses(String category, String location);
    
    /**
     * Fetches detailed information for a specific place using Place Details API.
     *
     * @param placeId The Google Place ID
     * @return A Business object with detailed information
     */
    Business getBusinessDetails(String placeId);
    
    /**
     * Extracts email from a website by crawling (if possible).
     *
     * @param website The website URL
     * @return The extracted email or null if not found
     */
    String extractEmail(String website);
} 