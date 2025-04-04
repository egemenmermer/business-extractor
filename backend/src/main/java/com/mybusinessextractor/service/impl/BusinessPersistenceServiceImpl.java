package com.mybusinessextractor.service.impl;

import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.service.BusinessPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for persisting business data in memory.
 * This version doesn't use a database for easier deployment.
 */
@Slf4j
@Service
public class BusinessPersistenceServiceImpl implements BusinessPersistenceService {

    // In-memory storage for businesses
    private final Map<String, Business> businessMap = new ConcurrentHashMap<>();

    /**
     * Save a business to the in-memory store.
     *
     * @param business the business model to save
     * @return the saved business
     */
    @Override
    public Business saveBusiness(Business business) {
        if (business == null || business.getId() == null) {
            log.warn("Cannot save null business or business with null ID");
            return null;
        }
        
        log.info("Saving business {} to in-memory store", business.getId());
        businessMap.put(business.getId(), business);
        return business;
    }

    /**
     * Save a list of businesses to the in-memory store.
     *
     * @param businesses the list of business models to save
     * @return the saved business list
     */
    @Override
    public List<Business> saveBusinesses(List<Business> businesses) {
        if (businesses == null || businesses.isEmpty()) {
            return Collections.emptyList();
        }
        
        log.info("Saving {} businesses to in-memory store", businesses.size());
        businesses.forEach(this::saveBusiness);
        return businesses;
    }

    /**
     * Find all businesses in the in-memory store.
     *
     * @return list of all businesses
     */
    @Override
    public List<Business> findAllBusinesses() {
        return new ArrayList<>(businessMap.values());
    }

    /**
     * Find businesses by category.
     *
     * @param category the category to search for
     * @return list of businesses in the given category
     */
    @Override
    public List<Business> findBusinessesByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return Collections.emptyList();
        }
        
        return businessMap.values().stream()
                .filter(business -> category.equalsIgnoreCase(business.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Find businesses by city.
     *
     * @param city the city to search for
     * @return list of businesses in the given city
     */
    @Override
    public List<Business> findBusinessesByCity(String city) {
        if (city == null || city.isEmpty()) {
            return Collections.emptyList();
        }
        
        return businessMap.values().stream()
                .filter(business -> city.equalsIgnoreCase(business.getCity()))
                .collect(Collectors.toList());
    }

    /**
     * Find businesses with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Override
    public List<Business> findBusinessesWithPagination(int page, int size) {
        List<Business> allBusinesses = new ArrayList<>(businessMap.values());
        return paginateList(allBusinesses, page, size);
    }

    /**
     * Find businesses by category with pagination.
     *
     * @param category the category to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Override
    public List<Business> findBusinessesByCategoryWithPagination(String category, int page, int size) {
        List<Business> categoryBusinesses = findBusinessesByCategory(category);
        return paginateList(categoryBusinesses, page, size);
    }

    /**
     * Find businesses by city with pagination.
     *
     * @param city the city to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Override
    public List<Business> findBusinessesByCityWithPagination(String city, int page, int size) {
        List<Business> cityBusinesses = findBusinessesByCity(city);
        return paginateList(cityBusinesses, page, size);
    }

    /**
     * Find businesses with email.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses with email addresses and metadata
     */
    @Override
    public PaginatedBusinessList findBusinessesWithEmail(int page, int size) {
        List<Business> businessesWithEmail = businessMap.values().stream()
                .filter(business -> business.getEmail() != null && !business.getEmail().isEmpty())
                .collect(Collectors.toList());
        
        List<Business> paginatedContent = paginateList(businessesWithEmail, page, size);
        return new PaginatedBusinessList(paginatedContent, businessesWithEmail.size(), size, page);
    }

    /**
     * Find businesses without email.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses without email addresses and metadata
     */
    @Override
    public PaginatedBusinessList findBusinessesWithoutEmail(int page, int size) {
        List<Business> businessesWithoutEmail = businessMap.values().stream()
                .filter(business -> business.getEmail() == null || business.getEmail().isEmpty())
                .collect(Collectors.toList());
        
        List<Business> paginatedContent = paginateList(businessesWithoutEmail, page, size);
        return new PaginatedBusinessList(paginatedContent, businessesWithoutEmail.size(), size, page);
    }

    /**
     * Find businesses by country.
     *
     * @param country the country to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses in the given country and metadata
     */
    @Override
    public PaginatedBusinessList findBusinessesByCountry(String country, int page, int size) {
        if (country == null || country.isEmpty()) {
            return new PaginatedBusinessList(Collections.emptyList(), 0, size, page);
        }
        
        List<Business> countryBusinesses = businessMap.values().stream()
                .filter(business -> country.equalsIgnoreCase(business.getCountry()))
                .collect(Collectors.toList());
        
        List<Business> paginatedContent = paginateList(countryBusinesses, page, size);
        return new PaginatedBusinessList(paginatedContent, countryBusinesses.size(), size, page);
    }

    /**
     * Helper method to paginate a list.
     */
    private <T> List<T> paginateList(List<T> list, int page, int size) {
        if (list == null || list.isEmpty() || page < 0 || size <= 0) {
            return Collections.emptyList();
        }
        
        int fromIndex = page * size;
        if (fromIndex > list.size()) {
            return Collections.emptyList();
        }
        
        int toIndex = Math.min(fromIndex + size, list.size());
        return list.subList(fromIndex, toIndex);
    }
} 