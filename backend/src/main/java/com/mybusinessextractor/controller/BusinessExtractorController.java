package com.mybusinessextractor.controller;

import com.mybusinessextractor.dto.ExportRequest;
import com.mybusinessextractor.dto.SearchRequest;
import com.mybusinessextractor.dto.SearchResponse;
import com.mybusinessextractor.dto.TaskStatus;
import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.service.BusinessExtractorService;
import com.mybusinessextractor.service.impl.BusinessPersistenceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.List;

/**
 * REST controller for the Business Extractor API.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusinessExtractorController {

    private final BusinessExtractorService businessExtractorService;
    private final BusinessPersistenceServiceImpl businessPersistenceService;

    /**
     * Initiates a search for businesses based on provided categories and locations.
     *
     * @param request the search request containing categories and locations
     * @param authentication the authenticated user
     * @return a response with the task ID
     */
    @PostMapping("/search")
    public ResponseEntity<String> search(@Valid @RequestBody SearchRequest request, Authentication authentication) {
        log.info("Received search request with {} categories and {} locations from user: {}", 
                request.getCategories().size(), request.getLocations().size(), authentication.getName());
        
        String taskId = businessExtractorService.initiateSearch(request);
        return ResponseEntity.ok(taskId);
    }

    /**
     * Gets the current status of tasks.
     *
     * @param authentication the authenticated user
     * @return a list of task statuses
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskStatus>> getTasks(Authentication authentication) {
        log.info("Fetching tasks for user: {}", authentication.getName());
        List<TaskStatus> tasks = businessExtractorService.getTaskStatus();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Gets the current search results.
     *
     * @param authentication the authenticated user
     * @return the search response containing businesses and status
     */
    @GetMapping("/results")
    public ResponseEntity<SearchResponse> getResults(Authentication authentication) {
        log.info("Fetching results for user: {}", authentication.getName());
        SearchResponse response = businessExtractorService.getResults();
        return ResponseEntity.ok(response);
    }

    /**
     * Exports the current results to a file.
     *
     * @param request the export request containing the format
     * @param authentication the authenticated user
     * @return the exported file as a downloadable resource
     */
    @PostMapping("/export")
    public ResponseEntity<Resource> exportResults(@Valid @RequestBody ExportRequest request, Authentication authentication) {
        log.info("Exporting results as {} for user: {}", request.getFormat(), authentication.getName());
        String format = request.getFormat().toLowerCase();
        String filePath = businessExtractorService.exportResults(format);
        
        // Create a Resource from the file path
        Resource resource = new FileSystemResource(filePath);
        
        // Set appropriate content type
        MediaType mediaType;
        if ("csv".equals(format)) {
            mediaType = MediaType.parseMediaType("text/csv");
        } else if ("excel".equals(format)) {
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        
        // Extract filename from path
        String filename = Paths.get(filePath).getFileName().toString();
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Retrieves businesses from the database.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param authentication the authenticated user
     * @return list of businesses for the requested page
     */
    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getStoredBusinesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("Retrieving businesses page {} with size {} for user: {}", page, size, authentication.getName());
        List<Business> businesses = businessPersistenceService.findBusinessesWithPagination(page, size);
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * Retrieves businesses from the database by category.
     *
     * @param category the business category to filter by
     * @param page the page number (0-based)
     * @param size the page size
     * @param authentication the authenticated user
     * @return list of businesses matching the category
     */
    @GetMapping("/businesses/category/{category}")
    public ResponseEntity<List<Business>> getBusinessesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("Retrieving businesses by category: {} (page {}, size {}) for user: {}", 
                category, page, size, authentication.getName());
        List<Business> businesses = businessPersistenceService.findBusinessesByCategoryWithPagination(category, page, size);
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * Retrieves businesses from the database by city.
     *
     * @param city the city to filter by
     * @param page the page number (0-based)
     * @param size the page size
     * @param authentication the authenticated user
     * @return list of businesses in the specified city
     */
    @GetMapping("/businesses/city/{city}")
    public ResponseEntity<List<Business>> getBusinessesByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("Retrieving businesses by city: {} (page {}, size {}) for user: {}", 
                city, page, size, authentication.getName());
        List<Business> businesses = businessPersistenceService.findBusinessesByCityWithPagination(city, page, size);
        return ResponseEntity.ok(businesses);
    }
} 