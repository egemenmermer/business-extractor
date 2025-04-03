package com.mybusinessextractor.repository;

import com.mybusinessextractor.entity.BusinessEntity;
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
     * Find businesses by city.
     *
     * @param city the city to search for
     * @return list of businesses in the given city
     */
    List<BusinessEntity> findByCity(String city);
    
    /**
     * Find businesses by category and city.
     *
     * @param category the category to search for
     * @param city the city to search for
     * @return list of businesses in the given category and city
     */
    List<BusinessEntity> findByCategoryAndCity(String category, String city);
} 