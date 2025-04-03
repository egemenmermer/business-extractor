import React, { useState, useMemo } from 'react';
import { useAppContext } from '../context/AppContext';
import { Business } from '../types';

const ResultsTable: React.FC = () => {
  const { 
    businesses, 
    storedBusinesses, 
    isPolling, 
    exportData, 
    isViewingStoredData 
  } = useAppContext();
  
  const [searchTerm, setSearchTerm] = useState('');
  const [sortColumn, setSortColumn] = useState<keyof Business>('businessName');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');

  // Use appropriate data source based on view
  const dataSource = isViewingStoredData ? storedBusinesses : businesses;

  // Filter businesses based on search term
  const filteredBusinesses = useMemo(() => {
    return dataSource.filter((business) => {
      const searchFields = [
        business.businessName,
        business.address,
        business.city,
        business.state,
        business.postalCode,
        business.country,
        business.phone,
        business.email,
        business.website,
        business.category,
      ].filter(Boolean);

      return searchFields.some((field) =>
        field?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    });
  }, [dataSource, searchTerm]);

  // Sort businesses based on column and direction
  const sortedBusinesses = useMemo(() => {
    return [...filteredBusinesses].sort((a, b) => {
      const aValue = a[sortColumn] || '';
      const bValue = b[sortColumn] || '';

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return sortDirection === 'asc'
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue);
      }

      // For numeric values
      if (sortDirection === 'asc') {
        return (aValue as any) - (bValue as any);
      } else {
        return (bValue as any) - (aValue as any);
      }
    });
  }, [filteredBusinesses, sortColumn, sortDirection]);

  const handleSort = (column: keyof Business) => {
    if (sortColumn === column) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortColumn(column);
      setSortDirection('asc');
    }
  };

  const renderSortIcon = (column: keyof Business) => {
    if (sortColumn !== column) return null;

    return (
      <span className="ml-1">
        {sortDirection === 'asc' ? '▲' : '▼'}
      </span>
    );
  };

  return (
    <div className="bg-gray-800 rounded-lg shadow p-4 h-full">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-white">
          {isViewingStoredData ? 'Stored Businesses' : 'Search Results'}
        </h2>
        <div className="flex space-x-2">
          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="px-3 py-1 bg-gray-700 text-white rounded focus:outline-none"
          />
          <button
            onClick={() => exportData('csv')}
            disabled={dataSource.length === 0 || isPolling}
            className="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Export CSV
          </button>
          <button
            onClick={() => exportData('excel')}
            disabled={dataSource.length === 0 || isPolling}
            className="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Export Excel
          </button>
        </div>
      </div>

      <div className="overflow-auto max-h-[calc(100vh-200px)]">
        {dataSource.length === 0 ? (
          <p className="text-gray-400 text-center py-8">
            {isViewingStoredData 
              ? 'No stored businesses found in the database' 
              : 'No search results to display'}
          </p>
        ) : (
          <table className="min-w-full bg-gray-700 rounded-lg overflow-hidden">
            <thead className="bg-gray-600">
              <tr>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('businessName')}
                >
                  Business Name {renderSortIcon('businessName')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('category')}
                >
                  Category {renderSortIcon('category')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('address')}
                >
                  Address {renderSortIcon('address')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('city')}
                >
                  City {renderSortIcon('city')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('state')}
                >
                  State {renderSortIcon('state')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('phone')}
                >
                  Phone {renderSortIcon('phone')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('email')}
                >
                  Email {renderSortIcon('email')}
                </th>
                <th 
                  className="px-4 py-2 text-left text-white cursor-pointer"
                  onClick={() => handleSort('website')}
                >
                  Website {renderSortIcon('website')}
                </th>
                <th className="px-4 py-2 text-left text-white">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-600">
              {sortedBusinesses.map((business) => (
                <tr key={business.id} className="hover:bg-gray-600">
                  <td className="px-4 py-2 text-white">{business.businessName}</td>
                  <td className="px-4 py-2 text-white">{business.category}</td>
                  <td className="px-4 py-2 text-white">{business.address}</td>
                  <td className="px-4 py-2 text-white">{business.city}</td>
                  <td className="px-4 py-2 text-white">{business.state}</td>
                  <td className="px-4 py-2 text-white">{business.phone}</td>
                  <td className="px-4 py-2 text-white">{business.email}</td>
                  <td className="px-4 py-2 text-white truncate max-w-[200px]">
                    {business.website}
                  </td>
                  <td className="px-4 py-2 text-white">
                    <a
                      href={business.mapsLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-400 hover:text-blue-300"
                    >
                      Map
                    </a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      <div className="mt-2 text-right text-gray-400">
        {filteredBusinesses.length} of {dataSource.length} records
      </div>
    </div>
  );
};

export default ResultsTable; 