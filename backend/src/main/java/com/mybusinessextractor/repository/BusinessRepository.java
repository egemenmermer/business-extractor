package com.mybusinessextractor.repository;

import com.mybusinessextractor.entity.BusinessEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Business entity operations.
 */
@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, String> {
    
    /**
     * Find businesses by category.
     *
     * @param category the category to search for
     * @return list of businesses in the given category
     */
    List<BusinessEntity> findByCategory(String category);
    
    /**
     * Find businesses by category with pagination.
     *
     * @param category the category to search for
     * @param pageable the pagination information
     * @return page of businesses in the given category
     */
    Page<BusinessEntity> findByCategory(String category, Pageable pageable);
    
    /**
     * Find businesses by city.
     *
     * @param city the city to search for
     * @return list of businesses in the given city
     */
    List<BusinessEntity> findByCity(String city);
    
    /**
     * Find businesses by city with pagination.
     *
     * @param city the city to search for
     * @param pageable the pagination information
     * @return page of businesses in the given city
     */
    Page<BusinessEntity> findByCity(String city, Pageable pageable);
    
    /**
     * Find businesses by category and city.
     *
     * @param category the category to search for
     * @param city the city to search for
     * @return list of businesses in the given category and city
     */
    List<BusinessEntity> findByCategoryAndCity(String category, String city);
    
    /**
     * Find businesses with email.
     *
     * @return list of businesses with email addresses
     */
    List<BusinessEntity> findByEmailIsNotNull();
    
    /**
     * Find businesses with email and pagination.
     *
     * @param pageable the pagination information
     * @return page of businesses with email addresses
     */
    Page<BusinessEntity> findByEmailIsNotNull(Pageable pageable);
    
    /**
     * Find businesses without email.
     *
     * @return list of businesses without email addresses
     */
    List<BusinessEntity> findByEmailIsNull();
    
    /**
     * Find businesses without email and pagination.
     *
     * @param pageable the pagination information
     * @return page of businesses without email addresses
     */
    Page<BusinessEntity> findByEmailIsNull(Pageable pageable);
    
    /**
     * Find businesses by country.
     *
     * @param country the country to search for
     * @return list of businesses in the given country
     */
    List<BusinessEntity> findByCountry(String country);
    
    /**
     * Find businesses by country with pagination.
     *
     * @param country the country to search for
     * @param pageable the pagination information
     * @return page of businesses in the given country
     */
    Page<BusinessEntity> findByCountry(String country, Pageable pageable);
} 