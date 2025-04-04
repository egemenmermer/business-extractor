package com.mybusinessextractor.controller;

import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.service.BusinessPersistenceService;
import com.mybusinessextractor.service.BusinessPersistenceService.PaginatedBusinessList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for business-related operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/businesses")
public class BusinessController {

    private final BusinessPersistenceService businessPersistenceService;

    /**
     * Filter businesses by email status.
     *
     * @param hasEmail boolean indicating whether to find businesses with or without email
     * @param page     the page number (0-based)
     * @param size     the page size
     * @return the businesses with or without email based on the filter
     */
    @GetMapping("/filter/email")
    public ResponseEntity<PaginatedBusinessList> filterBusinessesByEmail(
            @RequestParam(value = "hasEmail", required = true) boolean hasEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Filtering businesses by email status: {}", hasEmail ? "Has Email" : "No Email");
        
        PaginatedBusinessList businesses = hasEmail 
            ? businessPersistenceService.findBusinessesWithEmail(page, size)
            : businessPersistenceService.findBusinessesWithoutEmail(page, size);
        
        return ResponseEntity.ok(businesses);
    }

    /**
     * Filter businesses by country.
     *
     * @param country  the country to filter by
     * @param page     the page number (0-based)
     * @param size     the page size
     * @return the businesses in the specified country
     */
    @GetMapping("/filter/country")
    public ResponseEntity<PaginatedBusinessList> filterBusinessesByCountry(
            @RequestParam(value = "country", required = true) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("Filtering businesses by country: {}", country);
        
        PaginatedBusinessList businesses = businessPersistenceService.findBusinessesByCountry(country, page, size);
        
        return ResponseEntity.ok(businesses);
    }
} 