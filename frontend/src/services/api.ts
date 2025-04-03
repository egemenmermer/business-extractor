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

// Add interceptor to add auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Add interceptor to handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid, redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

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

// Auth API service
export const authAPI = {
  register: async (data: any) => {
    return api.post('/auth/register', data);
  },
  
  login: async (data: any) => {
    return api.post('/auth/login', data);
  },
  
  verifyEmail: async (token: string) => {
    return api.get(`/auth/verify-email?token=${token}`);
  },
  
  forgotPassword: async (email: string) => {
    return api.post('/auth/forgot-password', { email });
  },
  
  resetPassword: async (token: string, newPassword: string) => {
    return api.post('/auth/reset-password', { token, newPassword });
  },
};

// Business API service
export const businessAPI = {
  search: async (data: any) => {
    return api.post('/search', data);
  },
  
  getTasks: async () => {
    return api.get('/tasks');
  },
  
  getResults: async () => {
    return api.get('/results');
  },
  
  exportResults: async (format: string) => {
    return api.post('/export', { format }, { responseType: 'blob' });
  },
  
  getStoredBusinesses: async (page: number = 0, size: number = 50) => {
    return api.get(`/businesses?page=${page}&size=${size}`);
  },
  
  getBusinessesByCategory: async (category: string, page: number = 0, size: number = 50) => {
    return api.get(`/businesses/category/${encodeURIComponent(category)}?page=${page}&size=${size}`);
  },
  
  getBusinessesByCity: async (city: string, page: number = 0, size: number = 50) => {
    return api.get(`/businesses/city/${encodeURIComponent(city)}?page=${page}&size=${size}`);
  },
  
  getBusinessesByEmailStatus: async (hasEmail: boolean, page: number = 0, size: number = 50) => {
    return api.get(`/businesses/email-status?hasEmail=${hasEmail}&page=${page}&size=${size}`);
  },
  
  getBusinessesByCountry: async (country: string, page: number = 0, size: number = 50) => {
    return api.get(`/businesses/country/${encodeURIComponent(country)}?page=${page}&size=${size}`);
  },
};

export default api; 