package com.mybusinessextractor.service;

import com.mybusinessextractor.entity.BusinessEntity;
import com.mybusinessextractor.model.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for business persistence operations.
 */
public interface BusinessPersistenceService {

    /**
     * Save a business to the database.
     *
     * @param business the business model to save
     * @return the saved business entity
     */
    BusinessEntity saveBusiness(Business business);

    /**
     * Save a list of businesses to the database.
     *
     * @param businesses the list of business models to save
     * @return the saved business entities
     */
    List<BusinessEntity> saveBusinesses(List<Business> businesses);

    /**
     * Find all businesses in the database.
     *
     * @return list of all businesses
     */
    List<Business> findAllBusinesses();

    /**
     * Find businesses by category.
     *
     * @param category the category to search for
     * @return list of businesses in the given category
     */
    List<Business> findBusinessesByCategory(String category);

    /**
     * Find businesses by city.
     *
     * @param city the city to search for
     * @return list of businesses in the given city
     */
    List<Business> findBusinessesByCity(String city);

    /**
     * Find businesses with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    List<Business> findBusinessesWithPagination(int page, int size);

    /**
     * Find businesses by category with pagination.
     *
     * @param category the category to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    List<Business> findBusinessesByCategoryWithPagination(String category, int page, int size);

    /**
     * Find businesses by city with pagination.
     *
     * @param city the city to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    List<Business> findBusinessesByCityWithPagination(String city, int page, int size);
    
    /**
     * Find businesses with email.
     *
     * @param pageable the pagination information
     * @return page of businesses with email addresses
     */
    Page<Business> findBusinessesWithEmail(Pageable pageable);

    /**
     * Find businesses without email.
     *
     * @param pageable the pagination information
     * @return page of businesses without email addresses
     */
    Page<Business> findBusinessesWithoutEmail(Pageable pageable);

    /**
     * Find businesses by country.
     *
     * @param country the country to search for
     * @param pageable the pagination information
     * @return page of businesses in the given country
     */
    Page<Business> findBusinessesByCountry(String country, Pageable pageable);
} 