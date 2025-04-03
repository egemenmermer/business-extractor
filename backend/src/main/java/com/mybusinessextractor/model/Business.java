package com.mybusinessextractor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a business entity extracted from Google Places API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {
    private String id;
    private String businessName;
    private String realCategory;
    private String category;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String website;
    private Double latitude;
    private Double longitude;
    private String mapsLink;
    private String detailsLink;
} 