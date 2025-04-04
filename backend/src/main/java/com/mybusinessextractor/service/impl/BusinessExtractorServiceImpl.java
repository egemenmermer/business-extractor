package com.mybusinessextractor.service.impl;

import com.mybusinessextractor.dto.SearchRequest;
import com.mybusinessextractor.dto.SearchResponse;
import com.mybusinessextractor.dto.TaskStatus;
import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.service.BusinessExtractorService;
import com.mybusinessextractor.service.GooglePlacesService;
import com.mybusinessextractor.util.CountryCitiesUtil;
import com.mybusinessextractor.util.ExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of the BusinessExtractorService for extracting business data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessExtractorServiceImpl implements BusinessExtractorService {

    private final GooglePlacesService googlePlacesService;
    private final ExportUtil exportUtil;
    private final BusinessPersistenceServiceImpl businessPersistenceService;
    private final CountryCitiesUtil countryCitiesUtil;
    
    // In-memory storage for results and tasks
    private final List<Business> businessResults = new CopyOnWriteArrayList<>();
    private final Map<String, TaskStatus> taskStatuses = new ConcurrentHashMap<>();
    private final AtomicInteger taskIdCounter = new AtomicInteger(1);
    
    /**
     * Initiates a search for businesses based on provided categories and locations.
     * If a location is a country, it will be broken down into city-level searches.
     * 
     * @param request The search request containing categories and locations
     * @return A unique task ID for tracking
     */
    @Override
    public String initiateSearch(SearchRequest request) {
        // Clear previous results when starting a new search
        businessResults.clear();
        taskStatuses.clear();
        
        // Get categories and locations from the request
        List<String> categories = request.getCategories();
        List<String> locations = expandCountryLocations(request.getLocations());
        Boolean saveToDatabase = request.getSaveToDatabase() != null ? request.getSaveToDatabase() : true;
        
        log.info("Starting search with {} categories and {} locations (expanded from {})", 
                categories.size(), locations.size(), request.getLocations().size());
        
        // Create a task for each category-location combination
        categories.forEach(category -> 
            locations.forEach(location -> {
                String taskId = String.valueOf(taskIdCounter.getAndIncrement());
                TaskStatus taskStatus = TaskStatus.builder()
                        .id(taskId)
                        .category(category)
                        .location(location)
                        .status("PENDING")
                        .processedItems(0)
                        .totalItems(0)
                        .build();
                
                taskStatuses.put(taskId, taskStatus);
                
                // Process the task asynchronously
                processTask(taskId, category, location, saveToDatabase);
            })
        );
        
        return UUID.randomUUID().toString();
    }
    
    /**
     * Expands country locations to city-level locations.
     * If a location is a recognized country, it will be replaced with its cities.
     * Otherwise, the original location is kept.
     *
     * @param originalLocations The original list of locations
     * @return The expanded list of locations
     */
    private List<String> expandCountryLocations(List<String> originalLocations) {
        List<String> expandedLocations = new ArrayList<>();
        
        for (String location : originalLocations) {
            if (countryCitiesUtil.isCountry(location)) {
                log.info("Expanding country location: {} into individual cities", location);
                List<String> cities = countryCitiesUtil.getCitiesForCountry(location);
                expandedLocations.addAll(cities);
            } else {
                expandedLocations.add(location);
            }
        }
        
        return expandedLocations;
    }
    
    /**
     * Gets the current status of tasks.
     * 
     * @return A list of task statuses
     */
    @Override
    public List<TaskStatus> getTaskStatus() {
        return new ArrayList<>(taskStatuses.values());
    }
    
    /**
     * Gets the current search results.
     * 
     * @return The search response containing businesses and status
     */
    @Override
    public SearchResponse getResults() {
        // Calculate status based on task statuses
        boolean allCompleted = taskStatuses.values().stream()
                .allMatch(task -> "COMPLETED".equals(task.getStatus()) || "FAILED".equals(task.getStatus()));
        
        String status = allCompleted ? "COMPLETED" : "PROCESSING";
        
        return SearchResponse.builder()
                .businesses(new ArrayList<>(businessResults))
                .total(businessResults.size())
                .status(status)
                .build();
    }
    
    /**
     * Exports the current results to a file.
     * 
     * @param format The export format (csv or xlsx)
     * @return The path to the exported file
     */
    @Override
    public String exportResults(String format) {
        if (businessResults.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No results to export");
        }
        
        List<Business> results = new ArrayList<>(businessResults);
        
        if ("csv".equalsIgnoreCase(format)) {
            return exportUtil.exportToCsv(results);
        } else if ("xlsx".equalsIgnoreCase(format) || "excel".equalsIgnoreCase(format)) {
            return exportUtil.exportToExcel(results);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Unsupported export format. Use 'csv' or 'xlsx'");
        }
    }
    
    /**
     * Processes a task asynchronously.
     * 
     * @param taskId The task ID
     * @param category The business category
     * @param location The location to search
     * @param saveToDatabase Whether to save results to database
     */
    private void processTask(String taskId, String category, String location, boolean saveToDatabase) {
        // Update task status to PROCESSING
        TaskStatus taskStatus = taskStatuses.get(taskId);
        taskStatus.setStatus("PROCESSING");
        taskStatuses.put(taskId, taskStatus);
        
        Flux.fromIterable(Collections.singletonList(Mono.just(taskId)))
            .flatMap(m -> googlePlacesService.searchBusinesses(category, location))
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(business -> {
                // Set category for the business
                business.setCategory(category);
                business.setRealCategory(category);
                
                // Fetch additional details
                try {
                    Business detailedBusiness = googlePlacesService.getBusinessDetails(business.getId());
                    
                    // Merge the detailed business data with the basic business data
                    business.setBusinessName(detailedBusiness.getBusinessName());
                    business.setAddress(detailedBusiness.getAddress());
                    business.setCity(detailedBusiness.getCity());
                    business.setState(detailedBusiness.getState());
                    business.setPostalCode(detailedBusiness.getPostalCode());
                    business.setCountry(detailedBusiness.getCountry());
                    business.setPhone(detailedBusiness.getPhone());
                    business.setEmail(detailedBusiness.getEmail());
                    business.setWebsite(detailedBusiness.getWebsite());
                    
                    // Try to extract email from website if email is not available and website is provided
                    if ((business.getEmail() == null || business.getEmail().isEmpty()) && 
                        business.getWebsite() != null && !business.getWebsite().isEmpty()) {
                        try {
                            log.info("Attempting to extract email from website: {}", business.getWebsite());
                            String email = googlePlacesService.extractEmail(business.getWebsite());
                            if (email != null && !email.isEmpty()) {
                                log.info("Successfully extracted email {} from website {} for business {}", 
                                        email, business.getWebsite(), business.getBusinessName());
                                business.setEmail(email);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to extract email from website: {}", business.getWebsite(), e);
                        }
                    }
                    
                    // Update the processed items count
                    TaskStatus currentStatus = taskStatuses.get(taskId);
                    currentStatus.setProcessedItems(currentStatus.getProcessedItems() + 1);
                    taskStatuses.put(taskId, currentStatus);
                    
                } catch (Exception e) {
                    log.error("Error fetching details for business: {}", business.getId(), e);
                }
                
                // Add to results
                businessResults.add(business);
                
                // Save to database only if saveToDatabase flag is true
                if (saveToDatabase) {
                    try {
                        businessPersistenceService.saveBusiness(business);
                    } catch (Exception e) {
                        log.error("Error saving business to database: {}", business.getId(), e);
                    }
                }
            })
            .doOnComplete(() -> {
                // Update task status to COMPLETED
                TaskStatus currentStatus = taskStatuses.get(taskId);
                currentStatus.setStatus("COMPLETED");
                currentStatus.setTotalItems(currentStatus.getProcessedItems());
                taskStatuses.put(taskId, currentStatus);
                log.info("Task completed: {}", taskId);
            })
            .doOnError(e -> {
                // Update task status to FAILED
                TaskStatus currentStatus = taskStatuses.get(taskId);
                currentStatus.setStatus("FAILED");
                currentStatus.setMessage(e.getMessage());
                taskStatuses.put(taskId, currentStatus);
                log.error("Task failed: {}", taskId, e);
            })
            .subscribe();
    }
} 