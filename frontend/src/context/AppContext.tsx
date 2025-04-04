import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { Business } from '../types';
import { getStoredBusinesses, getBusinessesByCategory, getBusinessesByCity, getBusinessesByEmailStatus, getBusinessesByCountry } from '../services/api';
import { getTasks, getResults, exportResults as apiExportResults, searchBusinesses as apiSearchBusinesses } from '../services/api';
import { PaginatedResponse } from '../services/api';

// Interface definitions for context types
interface AppContextType {
  categories: string[];
  locations: string[];
  selectedCategories: string[];
  selectedLocations: string[];
  tasks: TaskStatus[];
  businesses: Business[];
  storedBusinesses: Business[];
  isLoading: boolean;
  isPolling: boolean;
  isLoadingMore: boolean;
  addCategory: (category: string) => void;
  removeCategory: (category: string) => void;
  addLocation: (location: string) => void;
  removeLocation: (location: string) => void;
  selectCategory: (category: string, selected: boolean) => void;
  selectLocation: (location: string, selected: boolean) => void;
  startSearch: () => Promise<void>;
  exportData: (format: string) => Promise<void>;
  loadStoredBusinesses: (page?: number) => Promise<void>;
  loadMoreBusinesses: () => Promise<void>;
  loadBusinessesByCategory: (category: string) => Promise<void>;
  loadMoreBusinessesByCategory: (category: string) => Promise<void>;
  loadBusinessesByCity: (city: string) => Promise<void>;
  loadMoreBusinessesByCity: (city: string) => Promise<void>;
  loadBusinessesByEmailStatus: (hasEmail: boolean, page?: number) => Promise<void>;
  loadBusinessesByCountry: (country: string, page?: number) => Promise<void>;
  resetFilters: () => void;
  isViewingStoredData: boolean;
  setIsViewingStoredData: (value: boolean) => void;
  currentCategory: string | null;
  currentCity: string | null;
  isEmailFilterActive: boolean;
  hasEmail: boolean | null;
  isCountryFilterActive: boolean;
  selectedCountry: string | null;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};

// Define the SearchRequest interface
interface SearchRequest {
  categories: string[];
  locations: string[];
}

interface TaskStatus {
  id: string;
  category: string;
  location: string;
  status: string;
  processedItems: number;
  totalItems: number;
  message?: string;
}

export const AppProvider: React.FC<{children: ReactNode}> = ({ children }) => {
  const [categories, setCategories] = useState<string[]>([]);
  const [locations, setLocations] = useState<string[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [selectedLocations, setSelectedLocations] = useState<string[]>([]);
  const [tasks, setTasks] = useState<TaskStatus[]>([]);
  const [businesses, setBusinesses] = useState<Business[]>([]);
  const [storedBusinesses, setStoredBusinesses] = useState<Business[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isPolling, setIsPolling] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [isViewingStoredData, setIsViewingStoredData] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const PAGE_SIZE = 50; // Number of businesses to load per page
  const [currentCategory, setCurrentCategory] = useState<string | null>(null);
  const [currentCity, setCurrentCity] = useState<string | null>(null);
  const [isEmailFilterActive, setIsEmailFilterActive] = useState<boolean>(false);
  const [hasEmail, setHasEmail] = useState<boolean | null>(null);
  const [isCountryFilterActive, setIsCountryFilterActive] = useState<boolean>(false);
  const [selectedCountry, setSelectedCountry] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Poll for tasks status when searching
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (isPolling) {
      interval = setInterval(async () => {
        try {
          const taskResults = await getTasks();
          setTasks(taskResults);
          
          const resultsData = await getResults();
          setBusinesses(resultsData.businesses);
          
          // Check if all tasks are completed or failed
          const allDone = taskResults.every((task: TaskStatus) => 
            task.status === 'COMPLETED' || task.status === 'FAILED'
          );
          
          if (allDone && taskResults.length > 0) {
            setIsPolling(false);
          }
        } catch (error) {
          console.error('Error polling for tasks:', error);
        }
      }, 2000);
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isPolling]);

  const addCategory = (category: string) => {
    if (!categories.includes(category)) {
      setCategories([...categories, category]);
    }
  };

  const removeCategory = (category: string) => {
    setCategories(categories.filter(c => c !== category));
    setSelectedCategories(selectedCategories.filter(c => c !== category));
  };

  const addLocation = (location: string) => {
    if (!locations.includes(location)) {
      setLocations([...locations, location]);
    }
  };

  const removeLocation = (location: string) => {
    setLocations(locations.filter(l => l !== location));
    setSelectedLocations(selectedLocations.filter(l => l !== location));
  };

  const selectCategory = (category: string, selected: boolean) => {
    if (selected) {
      if (!selectedCategories.includes(category)) {
        setSelectedCategories([...selectedCategories, category]);
      }
    } else {
      setSelectedCategories(selectedCategories.filter(c => c !== category));
    }
  };

  const selectLocation = (location: string, selected: boolean) => {
    if (selected) {
      if (!selectedLocations.includes(location)) {
        setSelectedLocations([...selectedLocations, location]);
      }
    } else {
      setSelectedLocations(selectedLocations.filter(l => l !== location));
    }
  };

  const startSearch = async () => {
    if (selectedCategories.length === 0 || selectedLocations.length === 0) {
      alert('Please select at least one category and one location');
      return;
    }

    setIsLoading(true);
    try {
      const request: SearchRequest = {
        categories: selectedCategories,
        locations: selectedLocations,
      };
      
      await apiSearchBusinesses(request);
      setIsPolling(true);
      
      // Initial fetch of tasks and results
      const taskResults = await getTasks();
      setTasks(taskResults);
      
      const resultsData = await getResults();
      setBusinesses(resultsData.businesses);
    } catch (error) {
      console.error('Error starting search:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const exportData = async (format: string) => {
    if (businesses.length === 0) {
      alert('No data to export');
      return;
    }

    try {
      setIsLoading(true);
      const blob = await apiExportResults(format);
      
      // Create download link
      const url = window.URL.createObjectURL(blob);
      const timestamp = new Date().toISOString().replace(/[-:]/g, '').split('.')[0];
      const filename = `business_export_${timestamp}.${format}`;
      
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error(`Error exporting data as ${format}:`, error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load stored businesses from the database (first page)
  const loadStoredBusinesses = async (page: number = 0) => {
    setIsLoading(true);
    try {
      // Reset filters when loading all businesses
      setCurrentCategory(null);
      setCurrentCity(null);
      setHasEmail(null);
      setSelectedCountry(null);
      setIsEmailFilterActive(false);
      setIsCountryFilterActive(false);
      
      // Reset pagination if loading first page
      if (page === 0) {
        setCurrentPage(0);
        const data = await getStoredBusinesses(0, PAGE_SIZE);
        setStoredBusinesses(data);
        setHasMore(data.length === PAGE_SIZE);
      } else {
        const data = await getStoredBusinesses(page, PAGE_SIZE);
        if (page === 0) {
          setStoredBusinesses(data);
        } else {
          setStoredBusinesses(prev => [...prev, ...data]);
        }
        setCurrentPage(page);
        setHasMore(data.length === PAGE_SIZE);
      }
      
      setIsViewingStoredData(true);
    } catch (error) {
      console.error('Error loading stored businesses:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load more businesses (next page)
  const loadMoreBusinesses = async () => {
    if (isLoadingMore) return;
    
    setIsLoadingMore(true);
    try {
      const nextPage = currentPage + 1;
      const data = await getStoredBusinesses(nextPage, PAGE_SIZE);
      
      if (data.length > 0) {
        setStoredBusinesses([...storedBusinesses, ...data]);
        setCurrentPage(nextPage);
      }
    } catch (error) {
      console.error('Error loading more businesses:', error);
    } finally {
      setIsLoadingMore(false);
    }
  };

  // Load businesses by category from the database
  const loadBusinessesByCategory = async (category: string) => {
    setIsLoading(true);
    try {
      // Reset pagination when loading businesses by category
      setCurrentPage(0);
      setCurrentCategory(category);
      setCurrentCity(null);
      const data = await getBusinessesByCategory(category, 0, PAGE_SIZE);
      setStoredBusinesses(data);
      setIsViewingStoredData(true);
    } catch (error) {
      console.error(`Error loading businesses by category ${category}:`, error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load more businesses by category from the database
  const loadMoreBusinessesByCategory = async (category: string) => {
    if (isLoadingMore) return;
    
    setIsLoadingMore(true);
    try {
      const nextPage = currentPage + 1;
      const data = await getBusinessesByCategory(category, nextPage, PAGE_SIZE);
      
      if (data.length > 0) {
        setStoredBusinesses([...storedBusinesses, ...data]);
        setCurrentPage(nextPage);
        setCurrentCategory(category);
      }
    } catch (error) {
      console.error(`Error loading more businesses by category ${category}:`, error);
    } finally {
      setIsLoadingMore(false);
    }
  };

  // Load businesses by city from the database
  const loadBusinessesByCity = async (city: string) => {
    setIsLoading(true);
    try {
      // Reset pagination when loading businesses by city
      setCurrentPage(0);
      setCurrentCity(city);
      setCurrentCategory(null);
      const data = await getBusinessesByCity(city, 0, PAGE_SIZE);
      setStoredBusinesses(data);
      setIsViewingStoredData(true);
    } catch (error) {
      console.error(`Error loading businesses by city ${city}:`, error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load more businesses by city from the database
  const loadMoreBusinessesByCity = async (city: string) => {
    if (isLoadingMore) return;
    
    setIsLoadingMore(true);
    try {
      const nextPage = currentPage + 1;
      const data = await getBusinessesByCity(city, nextPage, PAGE_SIZE);
      
      if (data.length > 0) {
        setStoredBusinesses([...storedBusinesses, ...data]);
        setCurrentPage(nextPage);
        setCurrentCity(city);
      }
    } catch (error) {
      console.error(`Error loading more businesses by city ${city}:`, error);
    } finally {
      setIsLoadingMore(false);
    }
  };

  // Load businesses by email status (with or without email)
  const loadBusinessesByEmailStatus = async (hasEmail: boolean, page: number = 0) => {
    try {
      setIsLoading(true);
      setIsEmailFilterActive(true);
      setHasEmail(hasEmail);
      setIsViewingStoredData(true);
      
      // Reset other filters
      setCurrentCategory(null);
      setCurrentCity(null);
      setIsCountryFilterActive(false);
      setSelectedCountry(null);
      
      try {
        const response = await getBusinessesByEmailStatus(hasEmail, page, PAGE_SIZE);
        
        if (page === 0) {
          setStoredBusinesses(response.content);
        } else {
          setStoredBusinesses(prev => [...prev, ...response.content]);
        }
        
        setCurrentPage(page);
        setHasMore(!response.last);
      } catch (error) {
        console.error('Error loading businesses by email status:', error);
      }
    } finally {
      setIsLoading(false);
    }
  };

  // Load businesses by country
  const loadBusinessesByCountry = async (country: string, page: number = 0) => {
    try {
      setIsLoading(true);
      setIsCountryFilterActive(true);
      setSelectedCountry(country);
      setIsViewingStoredData(true);
      
      // Reset other filters
      setCurrentCategory(null);
      setCurrentCity(null);
      setIsEmailFilterActive(false);
      setHasEmail(null);
      
      try {
        const response = await getBusinessesByCountry(country, page, PAGE_SIZE);
        
        if (page === 0) {
          setStoredBusinesses(response.content);
        } else {
          setStoredBusinesses(prev => [...prev, ...response.content]);
        }
        
        setCurrentPage(page);
        setHasMore(!response.last);
      } catch (error) {
        console.error('Error loading businesses by country:', error);
      }
    } finally {
      setIsLoading(false);
    }
  };

  // Reset all filters
  const resetFilters = () => {
    setCurrentCategory(null);
    setCurrentCity(null);
    setHasEmail(null);
    setSelectedCountry(null);
    setIsEmailFilterActive(false);
    setIsCountryFilterActive(false);
    loadStoredBusinesses(0);
  };

  const contextValue: AppContextType = {
    categories,
    locations,
    selectedCategories,
    selectedLocations,
    tasks,
    businesses,
    storedBusinesses,
    isLoading,
    isPolling,
    isLoadingMore,
    addCategory,
    removeCategory,
    addLocation,
    removeLocation,
    selectCategory,
    selectLocation,
    startSearch,
    exportData,
    loadStoredBusinesses,
    loadMoreBusinesses,
    loadBusinessesByCategory,
    loadMoreBusinessesByCategory,
    loadBusinessesByCity,
    loadMoreBusinessesByCity,
    loadBusinessesByEmailStatus,
    loadBusinessesByCountry,
    resetFilters,
    isViewingStoredData,
    setIsViewingStoredData,
    currentCategory,
    currentCity,
    isEmailFilterActive,
    hasEmail,
    isCountryFilterActive,
    selectedCountry,
  };

  return (
    <AppContext.Provider value={contextValue}>
      {children}
    </AppContext.Provider>
  );
}; 