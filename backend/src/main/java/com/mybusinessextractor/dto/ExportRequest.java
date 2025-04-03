package com.mybusinessextractor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for export requests specifying the format (CSV or Excel).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    @NotBlank(message = "Export format is required")
    private String format; // csv or excel
} 