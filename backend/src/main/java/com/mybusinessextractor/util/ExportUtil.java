package com.mybusinessextractor.util;

import com.mybusinessextractor.model.Business;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting business data to various formats.
 */
@Slf4j
@Component
public class ExportUtil {

    @Value("${export.csv.directory}")
    private String csvDirectory;

    @Value("${export.excel.directory}")
    private String excelDirectory;

    /**
     * Exports the list of businesses to a CSV file.
     *
     * @param businesses The list of businesses to export
     * @return The path to the exported CSV file
     */
    public String exportToCsv(List<Business> businesses) {
        try {
            // Create directory if it doesn't exist
            Path dirPath = Paths.get(csvDirectory);
            Files.createDirectories(dirPath);

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("business_export_%s.csv", timestamp);
            Path filePath = dirPath.resolve(fileName);

            // Write to CSV using BufferedWriter
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                // Write header
                writer.write("Id,BusinessName,RealCategory,Category,Address,City,State,PostalCode,Country,Phone,Email,Website,Latitude,Longitude,MapsLink,DetailsLink");
                writer.newLine();
                
                // Write data rows
                for (Business business : businesses) {
                    String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                            escapeField(business.getId()),
                            escapeField(business.getBusinessName()),
                            escapeField(business.getRealCategory()),
                            escapeField(business.getCategory()),
                            escapeField(business.getAddress()),
                            escapeField(business.getCity()),
                            escapeField(business.getState()),
                            escapeField(business.getPostalCode()),
                            escapeField(business.getCountry()),
                            escapeField(business.getPhone()),
                            escapeField(business.getEmail()),
                            escapeField(business.getWebsite()),
                            business.getLatitude() != null ? business.getLatitude().toString() : "",
                            business.getLongitude() != null ? business.getLongitude().toString() : "",
                            escapeField(business.getMapsLink()),
                            escapeField(business.getDetailsLink())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }

            log.info("CSV export completed: {}", filePath);
            return filePath.toString();
        } catch (Exception e) {
            log.error("Error exporting to CSV", e);
            throw new RuntimeException("Failed to export to CSV: " + e.getMessage(), e);
        }
    }
    
    /**
     * Escapes a field for CSV (handles nulls and escapes quotes)
     */
    private String escapeField(String field) {
        if (field == null) return "";
        return field.replace("\"", "\"\"");
    }

    /**
     * Exports the list of businesses to an Excel file.
     *
     * @param businesses The list of businesses to export
     * @return The path to the exported Excel file
     */
    public String exportToExcel(List<Business> businesses) {
        try {
            // Create directory if it doesn't exist
            Path dirPath = Paths.get(excelDirectory);
            Files.createDirectories(dirPath);

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("business_export_%s.xlsx", timestamp);
            Path filePath = dirPath.resolve(fileName);

            // Create workbook and sheet
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Business Data");

                // Create header row
                String[] headers = {
                        "Id", "BusinessName", "RealCategory", "Category", "Address", "City", "State", 
                        "PostalCode", "Country", "Phone", "Email", "Website", "Latitude", "Longitude", 
                        "MapsLink", "DetailsLink"
                };
                
                Row headerRow = sheet.createRow(0);
                
                // Create header style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // Add headers
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Add data rows
                int rowNum = 1;
                for (Business business : businesses) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(business.getId() != null ? business.getId() : "");
                    row.createCell(1).setCellValue(business.getBusinessName() != null ? business.getBusinessName() : "");
                    row.createCell(2).setCellValue(business.getRealCategory() != null ? business.getRealCategory() : "");
                    row.createCell(3).setCellValue(business.getCategory() != null ? business.getCategory() : "");
                    row.createCell(4).setCellValue(business.getAddress() != null ? business.getAddress() : "");
                    row.createCell(5).setCellValue(business.getCity() != null ? business.getCity() : "");
                    row.createCell(6).setCellValue(business.getState() != null ? business.getState() : "");
                    row.createCell(7).setCellValue(business.getPostalCode() != null ? business.getPostalCode() : "");
                    row.createCell(8).setCellValue(business.getCountry() != null ? business.getCountry() : "");
                    row.createCell(9).setCellValue(business.getPhone() != null ? business.getPhone() : "");
                    row.createCell(10).setCellValue(business.getEmail() != null ? business.getEmail() : "");
                    row.createCell(11).setCellValue(business.getWebsite() != null ? business.getWebsite() : "");
                    row.createCell(12).setCellValue(business.getLatitude() != null ? business.getLatitude() : 0.0);
                    row.createCell(13).setCellValue(business.getLongitude() != null ? business.getLongitude() : 0.0);
                    row.createCell(14).setCellValue(business.getMapsLink() != null ? business.getMapsLink() : "");
                    row.createCell(15).setCellValue(business.getDetailsLink() != null ? business.getDetailsLink() : "");
                }

                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                    workbook.write(outputStream);
                }
            }

            log.info("Excel export completed: {}", filePath);
            return filePath.toString();
        } catch (Exception e) {
            log.error("Error exporting to Excel", e);
            throw new RuntimeException("Failed to export to Excel: " + e.getMessage(), e);
        }
    }
} 