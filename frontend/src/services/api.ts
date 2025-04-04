import axios from 'axios';
import type { Business, SearchRequest, TaskStatus } from '../types/index';

// Use environment variable if available, otherwise fallback to default URL
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add PaginatedResponse type
export interface PaginatedResponse {
  content: Business[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export const searchBusinesses = async (request: SearchRequest): Promise<string> => {
  const response = await api.post('/search', request);
  return response.data;
};

export const getTasks = async (): Promise<TaskStatus[]> => {
  const response = await api.get('/tasks');
  return response.data;
};

export const getResults = async (): Promise<{
  businesses: Business[];
  total: number;
  status: string;
}> => {
  const response = await api.get('/results');
  return response.data;
};

export const exportResults = async (format: string): Promise<Blob> => {
  const response = await api.post('/export', { format }, {
    responseType: 'blob',
  });
  return response.data;
};

/**
 * Retrieves all businesses stored in the database
 */
export const getStoredBusinesses = async (page: number = 0, size: number = 50): Promise<Business[]> => {
  const response = await api.get('/businesses', {
    params: { page, size }
  });
  return response.data;
};

/**
 * Retrieves businesses from the database by category
 */
export const getBusinessesByCategory = async (category: string, page: number = 0, size: number = 50): Promise<Business[]> => {
  const response = await api.get(`/businesses/category/${encodeURIComponent(category)}`, {
    params: { page, size }
  });
  return response.data;
};

/**
 * Retrieves businesses from the database by city
 */
export const getBusinessesByCity = async (city: string, page: number = 0, size: number = 50): Promise<Business[]> => {
  const response = await api.get(`/businesses/city/${encodeURIComponent(city)}`, {
    params: { page, size }
  });
  return response.data;
};

// Function to get businesses by email status
export const getBusinessesByEmailStatus = async (
  hasEmail: boolean,
  page: number = 0,
  size: number = 50
): Promise<PaginatedResponse> => {
  try {
    const response = await fetch(
      `${API_URL}/businesses/filter/email?hasEmail=${hasEmail}&page=${page}&size=${size}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching businesses by email status:', error);
    throw error;
  }
};

// Function to get businesses by country
export const getBusinessesByCountry = async (
  country: string,
  page: number = 0,
  size: number = 50
): Promise<PaginatedResponse> => {
  try {
    const response = await fetch(
      `${API_URL}/businesses/filter/country?country=${encodeURIComponent(country)}&page=${page}&size=${size}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching businesses by country:', error);
    throw error;
  }
};

export default api; 