package com.mybusinessextractor.service;

import com.mybusinessextractor.dto.SearchRequest;
import com.mybusinessextractor.dto.SearchResponse;
import com.mybusinessextractor.dto.TaskStatus;
import com.mybusinessextractor.model.Business;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Service interface for business data extraction functionality.
 */
public interface BusinessExtractorService {
    
    /**
     * Initiates a search for businesses based on provided categories and locations.
     * 
     * @param request The search request containing categories and locations
     * @return A unique task ID for tracking
     */
    String initiateSearch(SearchRequest request);
    
    /**
     * Gets the current status of tasks.
     * 
     * @return A list of task statuses
     */
    List<TaskStatus> getTaskStatus();
    
    /**
     * Gets the current search results.
     * 
     * @return The search response containing businesses and status
     */
    SearchResponse getResults();
    
    /**
     * Exports the current results to a file.
     * 
     * @param format The export format (csv or excel)
     * @return The path to the exported file
     */
    String exportResults(String format);
} 