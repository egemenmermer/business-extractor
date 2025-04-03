package com.mybusinessextractor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for task status updates during data fetching.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {
    private String id;
    private String category;
    private String location;
    private String status; // "PENDING", "PROCESSING", "COMPLETED", "FAILED"
    private int processedItems;
    private int totalItems;
    private String message;
} 