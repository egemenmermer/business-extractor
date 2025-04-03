import React, { useState, useRef, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AppProvider } from './context/AppContext';
import CategoryPanel from './components/CategoryPanel';
import LocationPanel from './components/LocationPanel';
import TaskQueuePanel from './components/TaskQueuePanel';
import ResultsTable from './components/ResultsTable';
import LandingPage from './pages/LandingPage';
import Login from './pages/Login';
import Register from './pages/Register';
import EmailVerify from './pages/EmailVerify';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import './App.css';
import { useAppContext } from './context/AppContext';

// Auth check component for protected routes
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = localStorage.getItem('token');
  const location = useLocation();
  
  if (!token) {
    // Redirect to login if not authenticated
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  
  return <>{children}</>;
};

// Main App Content Component (separate to use context)
const AppContent: React.FC = () => {
  const { 
    startSearch, 
    isLoading, 
    isPolling, 
    isViewingStoredData, 
    setIsViewingStoredData,
    loadStoredBusinesses,
    loadBusinessesByCategory,
    loadBusinessesByCity,
    loadBusinessesByEmailStatus,
    loadBusinessesByCountry,
    resetFilters,
    currentCategory,
    currentCity,
    isEmailFilterActive,
    hasEmail,
    isCountryFilterActive,
    selectedCountry
  } = useAppContext();

  // References to form inputs for resetting
  const categoryInputRef = useRef<HTMLInputElement>(null);
  const cityInputRef = useRef<HTMLInputElement>(null);
  const emailSelectRef = useRef<HTMLSelectElement>(null);
  const countryInputRef = useRef<HTMLInputElement>(null);

  const handleViewStoredData = () => {
    setIsViewingStoredData(true);
    loadStoredBusinesses(0);
  };

  const handleNewSearch = () => {
    setIsViewingStoredData(false);
  };

  const handleClearFilters = () => {
    // Reset input values
    if (categoryInputRef.current) categoryInputRef.current.value = '';
    if (cityInputRef.current) cityInputRef.current.value = '';
    if (emailSelectRef.current) emailSelectRef.current.value = '';
    if (countryInputRef.current) countryInputRef.current.value = '';
    
    // Reset filters in context
    resetFilters();
  };

  return (
    <div className="flex flex-col h-screen p-4 bg-gray-900 text-white">
      <header className="mb-4 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">MyBusiness Extractor</h1>
          <p className="text-gray-400">Extract business data from Google Places API</p>
        </div>
        <div>
          <button 
            className="px-4 py-2 bg-red-600 text-white font-medium rounded hover:bg-red-700"
            onClick={() => {
              localStorage.removeItem('token');
              localStorage.removeItem('user');
              window.location.href = '/login';
            }}
          >
            Logout
          </button>
        </div>
      </header>

      <div className="mb-4">
        <div className="flex border-b border-gray-700">
          <button
            className={`px-4 py-2 font-medium ${!isViewingStoredData ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-400 hover:text-white'}`}
            onClick={handleNewSearch}
          >
            New Search
          </button>
          <button
            className={`px-4 py-2 font-medium ${isViewingStoredData ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-400 hover:text-white'}`}
            onClick={handleViewStoredData}
          >
            Stored Businesses
          </button>
        </div>
      </div>

      {!isViewingStoredData ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <div>
              <CategoryPanel />
            </div>
            <div>
              <LocationPanel />
            </div>
          </div>

          <div className="mb-4">
            <button
              onClick={startSearch}
              disabled={isLoading || isPolling}
              className="w-full py-3 bg-blue-600 text-white font-semibold rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? 'Processing...' : 'Get Data'}
            </button>
          </div>

          <div className="mb-4">
            <TaskQueuePanel />
          </div>
        </>
      ) : (
        <div className="mb-4">
          <div className="bg-gray-800 p-4 rounded-lg shadow">
            <h3 className="text-lg font-medium text-white mb-3">Advanced Filters</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Filter by Category */}
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">
                  Category
                </label>
                <input 
                  ref={categoryInputRef}
                  type="text" 
                  placeholder="Filter by category..." 
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={(e) => {
                    if (e.target.value) {
                      loadBusinessesByCategory(e.target.value);
                    } else {
                      loadStoredBusinesses(0);
                    }
                  }}
                />
              </div>
              
              {/* Filter by City */}
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">
                  City
                </label>
                <input 
                  ref={cityInputRef}
                  type="text" 
                  placeholder="Filter by city..." 
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={(e) => {
                    if (e.target.value) {
                      loadBusinessesByCity(e.target.value);
                    } else {
                      loadStoredBusinesses(0);
                    }
                  }}
                />
              </div>
              
              {/* Action buttons */}
              <div className="flex items-end">
                <button
                  className="px-4 py-2 bg-blue-600 text-white font-medium rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 ml-auto"
                  onClick={() => loadStoredBusinesses(0)}
                >
                  Refresh Data
                </button>
              </div>
            </div>
            
            {/* Additional filters - second row */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
              {/* Filter by Email Status */}
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">
                  Email Status
                </label>
                <select 
                  ref={emailSelectRef}
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  defaultValue=""
                  onChange={(e) => {
                    const value = e.target.value;
                    if (value === 'hasEmail') {
                      loadBusinessesByEmailStatus(true);
                    } else if (value === 'noEmail') {
                      loadBusinessesByEmailStatus(false);
                    } else {
                      loadStoredBusinesses(0);
                    }
                  }}
                >
                  <option value="">All</option>
                  <option value="hasEmail">Has Email</option>
                  <option value="noEmail">No Email</option>
                </select>
              </div>
              
              {/* Filter by Country */}
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">
                  Country
                </label>
                <input 
                  ref={countryInputRef}
                  type="text" 
                  placeholder="Filter by country..." 
                  className="w-full px-4 py-2 bg-gray-700 text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={(e) => {
                    if (e.target.value) {
                      loadBusinessesByCountry(e.target.value);
                    } else {
                      loadStoredBusinesses(0);
                    }
                  }}
                />
              </div>
              
              {/* Clear filters button */}
              <div className="flex items-end">
                <button
                  className="px-4 py-2 bg-gray-600 text-white font-medium rounded hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  onClick={handleClearFilters}
                >
                  Clear Filters
                </button>
              </div>
            </div>
            
            {/* Active Filters display */}
            <div className="mt-4 flex flex-wrap gap-2">
              {currentCategory && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-500 text-white">
                  Category: {currentCategory}
                  <button 
                    className="ml-2 text-white hover:text-blue-200"
                    onClick={() => {
                      loadStoredBusinesses(0);
                      if (categoryInputRef.current) {
                        categoryInputRef.current.value = '';
                      }
                    }}
                  >
                    ✕
                  </button>
                </span>
              )}
              {currentCity && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-500 text-white">
                  City: {currentCity}
                  <button 
                    className="ml-2 text-white hover:text-blue-200"
                    onClick={() => {
                      loadStoredBusinesses(0);
                      if (cityInputRef.current) {
                        cityInputRef.current.value = '';
                      }
                    }}
                  >
                    ✕
                  </button>
                </span>
              )}
              {isEmailFilterActive && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-500 text-white">
                  Email: {hasEmail ? 'Has Email' : 'No Email'}
                  <button 
                    className="ml-2 text-white hover:text-blue-200"
                    onClick={() => {
                      loadStoredBusinesses(0);
                      if (emailSelectRef.current) {
                        emailSelectRef.current.value = '';
                      }
                    }}
                  >
                    ✕
                  </button>
                </span>
              )}
              {isCountryFilterActive && selectedCountry && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-500 text-white">
                  Country: {selectedCountry}
                  <button 
                    className="ml-2 text-white hover:text-blue-200"
                    onClick={() => {
                      loadStoredBusinesses(0);
                      if (countryInputRef.current) {
                        countryInputRef.current.value = '';
                      }
                    }}
                  >
                    ✕
                  </button>
                </span>
              )}
            </div>
          </div>
        </div>
      )}

      <div className="flex-grow overflow-hidden">
        <ResultsTable />
      </div>
    </div>
  );
};

// App Wrapper with Provider and Routes
const App: React.FC = () => {
  // Check for authentication token on component mount 
  // and set up axios interceptor for token handling
  useEffect(() => {
    const token = localStorage.getItem('token');
    
    if (token) {
      // Set default Authorization header for all axios requests
      // This would normally be done in an axios interceptor
      // But for simplicity, we're just showing the concept here
    }
  }, []);
  
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/email-verify" element={<EmailVerify />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        
        {/* Protected Routes */}
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <AppProvider>
                <AppContent />
              </AppProvider>
            </ProtectedRoute>
          } 
        />
        
        {/* Catch all - redirect to landing */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
};

export default App;
