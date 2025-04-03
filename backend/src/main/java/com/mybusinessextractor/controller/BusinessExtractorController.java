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
     * @param request The search request containing categories and locations
     * @return A response with the task ID
     */
    @PostMapping("/search")
    public ResponseEntity<String> search(@Valid @RequestBody SearchRequest request) {
        log.info("Received search request with {} categories and {} locations", 
                request.getCategories().size(), request.getLocations().size());
        
        String taskId = businessExtractorService.initiateSearch(request);
        return ResponseEntity.ok(taskId);
    }

    /**
     * Gets the current status of tasks.
     *
     * @return A list of task statuses
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskStatus>> getTasks() {
        List<TaskStatus> tasks = businessExtractorService.getTaskStatus();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Gets the current search results.
     *
     * @return The search response containing businesses and status
     */
    @GetMapping("/results")
    public ResponseEntity<SearchResponse> getResults() {
        SearchResponse response = businessExtractorService.getResults();
        return ResponseEntity.ok(response);
    }

    /**
     * Exports the current results to a file.
     *
     * @param request The export request containing the format
     * @return The exported file as a downloadable resource
     */
    @PostMapping("/export")
    public ResponseEntity<Resource> exportResults(@Valid @RequestBody ExportRequest request) {
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
     * @return List of all stored businesses
     */
    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getStoredBusinesses() {
        log.info("Retrieving all stored businesses from database");
        List<Business> businesses = businessPersistenceService.findAllBusinesses();
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * Retrieves businesses from the database by category.
     *
     * @param category The business category to filter by
     * @return List of businesses matching the category
     */
    @GetMapping("/businesses/category/{category}")
    public ResponseEntity<List<Business>> getBusinessesByCategory(@PathVariable String category) {
        log.info("Retrieving businesses by category: {}", category);
        List<Business> businesses = businessPersistenceService.findBusinessesByCategory(category);
        return ResponseEntity.ok(businesses);
    }
    
    /**
     * Retrieves businesses from the database by city.
     *
     * @param city The city to filter by
     * @return List of businesses in the specified city
     */
    @GetMapping("/businesses/city/{city}")
    public ResponseEntity<List<Business>> getBusinessesByCity(@PathVariable String city) {
        log.info("Retrieving businesses by city: {}", city);
        List<Business> businesses = businessPersistenceService.findBusinessesByCity(city);
        return ResponseEntity.ok(businesses);
    }
} 