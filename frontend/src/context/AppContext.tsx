import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Business, SearchRequest, TaskStatus } from '../types';
import * as api from '../services/api';

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
  addCategory: (category: string) => void;
  removeCategory: (category: string) => void;
  addLocation: (location: string) => void;
  removeLocation: (location: string) => void;
  selectCategory: (category: string, selected: boolean) => void;
  selectLocation: (location: string, selected: boolean) => void;
  startSearch: () => Promise<void>;
  exportData: (format: string) => Promise<void>;
  loadStoredBusinesses: () => Promise<void>;
  loadBusinessesByCategory: (category: string) => Promise<void>;
  loadBusinessesByCity: (city: string) => Promise<void>;
  isViewingStoredData: boolean;
  setIsViewingStoredData: (value: boolean) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};

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
  const [isViewingStoredData, setIsViewingStoredData] = useState(false);

  // Poll for tasks status when searching
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (isPolling) {
      interval = setInterval(async () => {
        try {
          const tasks = await api.getTasks();
          setTasks(tasks);
          
          const results = await api.getResults();
          setBusinesses(results.businesses);
          
          // Check if all tasks are completed or failed
          const allDone = tasks.every(task => 
            task.status === 'COMPLETED' || task.status === 'FAILED'
          );
          
          if (allDone && tasks.length > 0) {
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
      
      await api.searchBusinesses(request);
      setIsPolling(true);
      
      // Initial fetch of tasks and results
      const tasks = await api.getTasks();
      setTasks(tasks);
      
      const results = await api.getResults();
      setBusinesses(results.businesses);
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
      const blob = await api.exportResults(format);
      
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

  // Load stored businesses from the database
  const loadStoredBusinesses = async () => {
    setIsLoading(true);
    try {
      const data = await api.getStoredBusinesses();
      setStoredBusinesses(data);
      setIsViewingStoredData(true);
    } catch (error) {
      console.error('Error loading stored businesses:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load businesses by category from the database
  const loadBusinessesByCategory = async (category: string) => {
    setIsLoading(true);
    try {
      const data = await api.getBusinessesByCategory(category);
      setStoredBusinesses(data);
      setIsViewingStoredData(true);
    } catch (error) {
      console.error(`Error loading businesses by category ${category}:`, error);
    } finally {
      setIsLoading(false);
    }
  };

  // Load businesses by city from the database
  const loadBusinessesByCity = async (city: string) => {
    setIsLoading(true);
    try {
      const data = await api.getBusinessesByCity(city);
      setStoredBusinesses(data);
      setIsViewingStoredData(true);
    } catch (error) {
      console.error(`Error loading businesses by city ${city}:`, error);
    } finally {
      setIsLoading(false);
    }
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
    addCategory,
    removeCategory,
    addLocation,
    removeLocation,
    selectCategory,
    selectLocation,
    startSearch,
    exportData,
    loadStoredBusinesses,
    loadBusinessesByCategory,
    loadBusinessesByCity,
    isViewingStoredData,
    setIsViewingStoredData,
  };

  return (
    <AppContext.Provider value={contextValue}>
      {children}
    </AppContext.Provider>
  );
}; 