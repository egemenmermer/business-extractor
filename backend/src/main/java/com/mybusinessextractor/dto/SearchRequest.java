package com.mybusinessextractor.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for search requests containing categories and locations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    @NotEmpty(message = "At least one category is required")
    private List<String> categories;
    
    @NotEmpty(message = "At least one location is required")
    private List<String> locations;
} 