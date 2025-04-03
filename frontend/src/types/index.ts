export interface Business {
  id: string;
  businessName: string;
  realCategory: string;
  category: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone: string;
  email: string;
  website: string;
  latitude: number;
  longitude: number;
  mapsLink: string;
  detailsLink: string;
}

export interface SearchRequest {
  categories: string[];
  locations: string[];
}

export interface TaskStatus {
  id: string;
  category: string;
  location: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  processedItems: number;
  totalItems: number;
  message?: string;
}

export interface SearchResponse {
  businesses: Business[];
  total: number;
  status: string;
} 