package com.mybusinessextractor.dto;

import com.mybusinessextractor.model.Business;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for search responses containing the list of businesses and status information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private List<Business> businesses;
    private int total;
    private String status;
} 