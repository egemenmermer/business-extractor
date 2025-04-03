import React, { useState } from 'react';
import { useAppContext } from '../context/AppContext';

const LocationPanel: React.FC = () => {
  const {
    locations,
    selectedLocations,
    addLocation,
    removeLocation,
    selectLocation,
  } = useAppContext();
  
  const [newLocation, setNewLocation] = useState('');

  const handleAddLocation = () => {
    if (newLocation.trim()) {
      addLocation(newLocation.trim());
      setNewLocation('');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleAddLocation();
    }
  };

  return (
    <div className="bg-gray-800 rounded-lg shadow p-4 h-full">
      <h2 className="text-xl font-bold mb-4 text-white">Locations</h2>
      
      <div className="flex mb-4">
        <input
          type="text"
          value={newLocation}
          onChange={(e) => setNewLocation(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Enter location"
          className="flex-grow px-3 py-2 bg-gray-700 text-white rounded-l focus:outline-none"
        />
        <button
          onClick={handleAddLocation}
          className="px-4 py-2 bg-blue-600 text-white font-semibold rounded-r hover:bg-blue-700"
        >
          Add
        </button>
      </div>
      
      <div className="overflow-y-auto max-h-[calc(100vh-280px)]">
        {locations.length === 0 ? (
          <p className="text-gray-400 text-center py-4">No locations added</p>
        ) : (
          <ul className="space-y-2">
            {locations.map((location) => (
              <li
                key={location}
                className="flex items-center bg-gray-700 rounded p-2"
              >
                <input
                  type="checkbox"
                  id={`location-${location}`}
                  checked={selectedLocations.includes(location)}
                  onChange={(e) => selectLocation(location, e.target.checked)}
                  className="mr-2 h-4 w-4"
                />
                <label
                  htmlFor={`location-${location}`}
                  className="flex-grow text-white cursor-pointer"
                >
                  {location}
                </label>
                <button
                  onClick={() => removeLocation(location)}
                  className="text-red-400 hover:text-red-500"
                >
                  <span className="sr-only">Remove</span>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 w-5"
                    viewBox="0 0 20 20"
                    fill="currentColor"
                  >
                    <path
                      fillRule="evenodd"
                      d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                      clipRule="evenodd"
                    />
                  </svg>
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
      
      <div className="flex justify-between mt-4">
        <button
          onClick={() => {
            locations.forEach(location => selectLocation(location, true));
          }}
          className="px-3 py-1 bg-gray-600 text-white text-sm rounded hover:bg-gray-700"
        >
          Select All
        </button>
        <button
          onClick={() => {
            locations.forEach(location => selectLocation(location, false));
          }}
          className="px-3 py-1 bg-gray-600 text-white text-sm rounded hover:bg-gray-700"
        >
          Clear Selection
        </button>
      </div>
    </div>
  );
};

export default LocationPanel; 