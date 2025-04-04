package com.mybusinessextractor.service;

import com.mybusinessextractor.model.Business;
import java.util.List;

/**
 * Service interface for business persistence operations.
 */
public interface BusinessPersistenceService {

    /**
     * Save a business to the storage.
     *
     * @param business the business model to save
     * @return the saved business
     */
    Business saveBusiness(Business business);

    /**
     * Save a list of businesses to the storage.
     *
     * @param businesses the list of business models to save
     * @return the saved business list
     */
    List<Business> saveBusinesses(List<Business> businesses);

    /**
     * Find all businesses in the storage.
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
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses with email addresses and metadata
     */
    PaginatedBusinessList findBusinessesWithEmail(int page, int size);

    /**
     * Find businesses without email.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses without email addresses and metadata
     */
    PaginatedBusinessList findBusinessesWithoutEmail(int page, int size);

    /**
     * Find businesses by country.
     *
     * @param country the country to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of businesses in the given country and metadata
     */
    PaginatedBusinessList findBusinessesByCountry(String country, int page, int size);
    
    /**
     * Represents a paginated list of businesses with metadata.
     */
    class PaginatedBusinessList {
        private List<Business> content;
        private int totalElements;
        private int totalPages;
        private boolean last;
        
        public PaginatedBusinessList(List<Business> content, int totalElements, int pageSize, int currentPage) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
            this.last = currentPage >= this.totalPages - 1;
        }
        
        public List<Business> getContent() {
            return content;
        }
        
        public int getTotalElements() {
            return totalElements;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public boolean isLast() {
            return last;
        }
    }
} 