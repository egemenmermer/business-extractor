package com.mybusinessextractor.service.impl;

import com.mybusinessextractor.entity.BusinessEntity;
import com.mybusinessextractor.model.Business;
import com.mybusinessextractor.repository.BusinessRepository;
import com.mybusinessextractor.service.BusinessPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for persisting business data to the database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessPersistenceServiceImpl implements BusinessPersistenceService {

    private final BusinessRepository businessRepository;

    /**
     * Save a business to the database.
     *
     * @param business the business model to save
     * @return the saved business entity
     */
    @Transactional
    public BusinessEntity saveBusiness(Business business) {
        // Check if business already exists in database
        if (business.getId() != null) {
            BusinessEntity existingEntity = businessRepository.findById(business.getId()).orElse(null);
            
            if (existingEntity != null) {
                log.info("Updating existing business: {}", business.getId());
                
                // Update entity with new information, only if the new data is not null/empty
                if (business.getBusinessName() != null && !business.getBusinessName().isEmpty()) {
                    existingEntity.setBusinessName(business.getBusinessName());
                }
                
                if (business.getRealCategory() != null && !business.getRealCategory().isEmpty()) {
                    existingEntity.setRealCategory(business.getRealCategory());
                }
                
                if (business.getCategory() != null && !business.getCategory().isEmpty()) {
                    existingEntity.setCategory(business.getCategory());
                }
                
                if (business.getAddress() != null && !business.getAddress().isEmpty()) {
                    existingEntity.setAddress(business.getAddress());
                }
                
                if (business.getCity() != null && !business.getCity().isEmpty()) {
                    existingEntity.setCity(business.getCity());
                }
                
                if (business.getState() != null && !business.getState().isEmpty()) {
                    existingEntity.setState(business.getState());
                }
                
                if (business.getPostalCode() != null && !business.getPostalCode().isEmpty()) {
                    existingEntity.setPostalCode(business.getPostalCode());
                }
                
                if (business.getCountry() != null && !business.getCountry().isEmpty()) {
                    existingEntity.setCountry(business.getCountry());
                }
                
                if (business.getPhone() != null && !business.getPhone().isEmpty()) {
                    existingEntity.setPhone(business.getPhone());
                }
                
                // Only update email if the new one is not null/empty and either:
                // 1. The existing email is null/empty, or
                // 2. The new email is different and seems more complete
                if (business.getEmail() != null && !business.getEmail().isEmpty() && 
                    (existingEntity.getEmail() == null || existingEntity.getEmail().isEmpty() || 
                    (!business.getEmail().equals(existingEntity.getEmail()) && 
                     business.getEmail().length() > existingEntity.getEmail().length()))) {
                    existingEntity.setEmail(business.getEmail());
                    log.info("Updated email for business {}: {}", business.getId(), business.getEmail());
                }
                
                // Only update website if the new one is not null/empty and either:
                // 1. The existing website is null/empty, or 
                // 2. The new website is different and seems more complete
                if (business.getWebsite() != null && !business.getWebsite().isEmpty() && 
                    (existingEntity.getWebsite() == null || existingEntity.getWebsite().isEmpty() ||
                    (!business.getWebsite().equals(existingEntity.getWebsite()) && 
                     business.getWebsite().length() > existingEntity.getWebsite().length()))) {
                    existingEntity.setWebsite(business.getWebsite());
                    log.info("Updated website for business {}: {}", business.getId(), business.getWebsite());
                }
                
                if (business.getLatitude() != null) {
                    existingEntity.setLatitude(business.getLatitude());
                }
                
                if (business.getLongitude() != null) {
                    existingEntity.setLongitude(business.getLongitude());
                }
                
                if (business.getMapsLink() != null && !business.getMapsLink().isEmpty()) {
                    existingEntity.setMapsLink(business.getMapsLink());
                }
                
                if (business.getDetailsLink() != null && !business.getDetailsLink().isEmpty()) {
                    existingEntity.setDetailsLink(business.getDetailsLink());
                }
                
                return businessRepository.save(existingEntity);
            }
        }
        
        // If business doesn't exist, create a new entity
        log.info("Creating new business: {}", business.getId());
        BusinessEntity entity = mapToEntity(business);
        return businessRepository.save(entity);
    }

    /**
     * Save a list of businesses to the database.
     *
     * @param businesses the list of business models to save
     * @return the saved business entities
     */
    @Transactional
    public List<BusinessEntity> saveBusinesses(List<Business> businesses) {
        List<BusinessEntity> entities = businesses.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        return businessRepository.saveAll(entities);
    }

    /**
     * Find all businesses in the database.
     *
     * @return list of all businesses
     */
    @Transactional(readOnly = true)
    public List<Business> findAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses by category.
     *
     * @param category the category to search for
     * @return list of businesses in the given category
     */
    @Transactional(readOnly = true)
    public List<Business> findBusinessesByCategory(String category) {
        return businessRepository.findByCategory(category).stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses by city.
     *
     * @param city the city to search for
     * @return list of businesses in the given city
     */
    @Transactional(readOnly = true)
    public List<Business> findBusinessesByCity(String city) {
        return businessRepository.findByCity(city).stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Transactional(readOnly = true)
    public List<Business> findBusinessesWithPagination(int page, int size) {
        // Create a PageRequest object with page number and size
        org.springframework.data.domain.PageRequest pageRequest = 
            org.springframework.data.domain.PageRequest.of(page, size);
        
        return businessRepository.findAll(pageRequest).getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses by category with pagination.
     *
     * @param category the category to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Transactional(readOnly = true)
    public List<Business> findBusinessesByCategoryWithPagination(String category, int page, int size) {
        org.springframework.data.domain.PageRequest pageRequest = 
            org.springframework.data.domain.PageRequest.of(page, size);
        
        return businessRepository.findByCategory(category, pageRequest).getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses by city with pagination.
     *
     * @param city the city to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of businesses for the requested page
     */
    @Transactional(readOnly = true)
    public List<Business> findBusinessesByCityWithPagination(String city, int page, int size) {
        org.springframework.data.domain.PageRequest pageRequest = 
            org.springframework.data.domain.PageRequest.of(page, size);
        
        return businessRepository.findByCity(city, pageRequest).getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    /**
     * Find businesses with email.
     *
     * @param pageable the pagination information
     * @return page of businesses with email addresses
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Business> findBusinessesWithEmail(Pageable pageable) {
        Page<BusinessEntity> entityPage = businessRepository.findByEmailIsNotNull(pageable);
        List<Business> businesses = entityPage.getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
        return new PageImpl<>(businesses, pageable, entityPage.getTotalElements());
    }

    /**
     * Find businesses without email.
     *
     * @param pageable the pagination information
     * @return page of businesses without email addresses
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Business> findBusinessesWithoutEmail(Pageable pageable) {
        Page<BusinessEntity> entityPage = businessRepository.findByEmailIsNull(pageable);
        List<Business> businesses = entityPage.getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
        return new PageImpl<>(businesses, pageable, entityPage.getTotalElements());
    }

    /**
     * Find businesses by country.
     *
     * @param country the country to search for
     * @param pageable the pagination information
     * @return page of businesses in the given country
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Business> findBusinessesByCountry(String country, Pageable pageable) {
        Page<BusinessEntity> entityPage = businessRepository.findByCountry(country, pageable);
        List<Business> businesses = entityPage.getContent().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
        return new PageImpl<>(businesses, pageable, entityPage.getTotalElements());
    }

    /**
     * Map a business model to a business entity.
     *
     * @param business the business model
     * @return the business entity
     */
    private BusinessEntity mapToEntity(Business business) {
        return BusinessEntity.builder()
                .id(business.getId())
                .businessName(business.getBusinessName())
                .realCategory(business.getRealCategory())
                .category(business.getCategory())
                .address(business.getAddress())
                .city(business.getCity())
                .state(business.getState())
                .postalCode(business.getPostalCode())
                .country(business.getCountry())
                .phone(business.getPhone())
                .email(business.getEmail())
                .website(business.getWebsite())
                .latitude(business.getLatitude())
                .longitude(business.getLongitude())
                .mapsLink(business.getMapsLink())
                .detailsLink(business.getDetailsLink())
                .build();
    }

    /**
     * Map a business entity to a business model.
     *
     * @param entity the business entity
     * @return the business model
     */
    private Business mapToModel(BusinessEntity entity) {
        return Business.builder()
                .id(entity.getId())
                .businessName(entity.getBusinessName())
                .realCategory(entity.getRealCategory())
                .category(entity.getCategory())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .mapsLink(entity.getMapsLink())
                .detailsLink(entity.getDetailsLink())
                .build();
    }
} 