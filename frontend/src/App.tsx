import React, { useState, useRef } from 'react';
import { AppProvider } from './context/AppContext';
import CategoryPanel from './components/CategoryPanel';
import LocationPanel from './components/LocationPanel';
import TaskQueuePanel from './components/TaskQueuePanel';
import ResultsTable from './components/ResultsTable';
import './App.css';
import { useAppContext } from './context/AppContext';

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
    selectedCountry,
    saveToDatabase,
    setSaveToDatabase,
    businesses,
    exportData
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
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-8 text-center">MyBusiness Extractor</h1>
        
        <div className="flex space-x-4 mb-4">
          <button
            onClick={handleNewSearch}
            className={`px-4 py-2 rounded ${
              !isViewingStoredData ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'
            }`}
          >
            New Search
          </button>
          <button
            onClick={handleViewStoredData}
            className={`px-4 py-2 rounded ${
              isViewingStoredData ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'
            }`}
          >
            Stored Businesses
          </button>
        </div>
        
        {!isViewingStoredData ? (
          // New Search View
          <>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
              <CategoryPanel />
              <LocationPanel />
              <TaskQueuePanel />
            </div>
            
            <div className="mb-6">
              <div className="flex flex-col md:flex-row items-center justify-between bg-gray-800 rounded-lg p-4">
                <div className="mb-4 md:mb-0">
                  <div className="flex items-center space-x-4 mb-2">
                    <button
                      onClick={startSearch}
                      disabled={isLoading || isPolling}
                      className={`px-6 py-2 rounded font-bold ${
                        isLoading || isPolling
                          ? 'bg-gray-600 cursor-not-allowed'
                          : 'bg-green-600 hover:bg-green-700'
                      }`}
                    >
                      {isLoading ? 'Loading...' : isPolling ? 'Processing...' : 'Get Data'}
                    </button>
                  </div>
                </div>
                
                <div className="flex space-x-2">
                  <button
                    onClick={() => exportData('csv')}
                    disabled={businesses.length === 0 || isLoading}
                    className={`px-4 py-2 rounded ${
                      businesses.length === 0 || isLoading
                        ? 'bg-gray-700 text-gray-500 cursor-not-allowed'
                        : 'bg-blue-600 hover:bg-blue-700 text-white'
                    }`}
                  >
                    Export CSV
                  </button>
                  <button
                    onClick={() => exportData('xlsx')}
                    disabled={businesses.length === 0 || isLoading}
                    className={`px-4 py-2 rounded ${
                      businesses.length === 0 || isLoading
                        ? 'bg-gray-700 text-gray-500 cursor-not-allowed'
                        : 'bg-blue-600 hover:bg-blue-700 text-white'
                    }`}
                  >
                    Export Excel
                  </button>
                </div>
              </div>
            </div>
            
            {businesses.length > 0 && (
              <div className="bg-gray-800 rounded-lg p-6">
                <ResultsTable />
              </div>
            )}
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
    </div>
  );
};

// App Wrapper with Provider
const App: React.FC = () => {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
};

export default App;
