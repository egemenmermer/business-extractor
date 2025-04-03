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
export const getStoredBusinesses = async (): Promise<Business[]> => {
  const response = await api.get('/businesses');
  return response.data;
};

/**
 * Retrieves businesses from the database by category
 */
export const getBusinessesByCategory = async (category: string): Promise<Business[]> => {
  const response = await api.get(`/businesses/category/${encodeURIComponent(category)}`);
  return response.data;
};

/**
 * Retrieves businesses from the database by city
 */
export const getBusinessesByCity = async (city: string): Promise<Business[]> => {
  const response = await api.get(`/businesses/city/${encodeURIComponent(city)}`);
  return response.data;
};

export default api; 