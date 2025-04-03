import React from 'react';
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
    loadStoredBusinesses
  } = useAppContext();

  const handleViewStoredData = () => {
    setIsViewingStoredData(true);
    loadStoredBusinesses();
  };

  const handleNewSearch = () => {
    setIsViewingStoredData(false);
  };

  return (
    <div className="flex flex-col h-screen p-4 bg-gray-900 text-white">
      <header className="mb-4">
        <h1 className="text-2xl font-bold">MyBusiness Extractor</h1>
        <p className="text-gray-400">Extract business data from Google Places API</p>
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
          <div className="flex gap-2 mb-2">
            <input 
              type="text" 
              placeholder="Filter by category or city..." 
              className="flex-grow px-4 py-2 bg-gray-800 text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button
              className="px-4 py-2 bg-blue-600 text-white font-medium rounded hover:bg-blue-700"
              onClick={loadStoredBusinesses}
            >
              Refresh Data
            </button>
          </div>
        </div>
      )}

      <div className="flex-grow overflow-hidden">
        <ResultsTable />
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
