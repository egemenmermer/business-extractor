import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Landing page component with marketing content and call-to-actions.
 */
const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen bg-gray-900 text-white">
      {/* Hero Section */}
      <header className="bg-gradient-to-r from-blue-900 to-purple-900 py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row items-center justify-between">
            <div className="md:w-1/2 mb-10 md:mb-0">
              <h1 className="text-4xl md:text-5xl font-extrabold leading-tight mb-4">
                Unlock Business Data with Our Powerful Extraction Tool
              </h1>
              <p className="text-xl text-blue-100 mb-8">
                Find, extract, and analyze business information from around the world. 
                Perfect for lead generation, market research, and competitor analysis.
              </p>
              <div className="flex space-x-4">
                <Link 
                  to="/register" 
                  className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition duration-300"
                >
                  Get Started Free
                </Link>
                <Link 
                  to="/login" 
                  className="bg-transparent border border-white hover:bg-white hover:text-blue-900 text-white font-bold py-3 px-6 rounded-lg transition duration-300"
                >
                  Log In
                </Link>
              </div>
            </div>
            <div className="md:w-1/2">
              <img 
                src="/images/dashboard-preview.png" 
                alt="Business Extractor Dashboard Preview" 
                className="rounded-lg shadow-2xl"
                onError={(e) => {
                  e.currentTarget.src = 'https://via.placeholder.com/600x400?text=Business+Extractor';
                }}
              />
            </div>
          </div>
        </div>
      </header>

      {/* Features Section */}
      <section className="py-16 px-4 sm:px-6 lg:px-8 bg-gray-800">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl font-bold text-center mb-12">Powerful Features for Business Discovery</h2>
          
          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-gray-700 p-6 rounded-lg shadow-lg">
              <div className="h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Advanced Search</h3>
              <p className="text-gray-300">
                Search businesses by category and location with our powerful filtering system. 
                Find exactly what you're looking for in seconds.
              </p>
            </div>
            
            <div className="bg-gray-700 p-6 rounded-lg shadow-lg">
              <div className="h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7v10c0 2 1 3 3 3h10c2 0 3-1 3-3V7c0-2-1-3-3-3H7c-2 0-3 1-3 3z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 11l3 3 5-5" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Data Extraction</h3>
              <p className="text-gray-300">
                Automatically extract contact information, addresses, and other important 
                business details with our intelligent scraping engine.
              </p>
            </div>
            
            <div className="bg-gray-700 p-6 rounded-lg shadow-lg">
              <div className="h-12 w-12 bg-blue-600 rounded-lg flex items-center justify-center mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Export Options</h3>
              <p className="text-gray-300">
                Export your results in CSV or Excel format for easy integration with your existing 
                workflow and business tools.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="py-16 px-4 sm:px-6 lg:px-8 bg-gray-900">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl font-bold text-center mb-12">How It Works</h2>
          
          <div className="grid md:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-xl font-bold">1</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">Sign Up</h3>
              <p className="text-gray-400">
                Create an account to get started with your business data journey.
              </p>
            </div>
            
            <div className="text-center">
              <div className="h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-xl font-bold">2</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">Search</h3>
              <p className="text-gray-400">
                Enter your target categories and locations to start your search.
              </p>
            </div>
            
            <div className="text-center">
              <div className="h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-xl font-bold">3</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">Extract</h3>
              <p className="text-gray-400">
                Our system automatically extracts and organizes business data for you.
              </p>
            </div>
            
            <div className="text-center">
              <div className="h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-xl font-bold">4</span>
              </div>
              <h3 className="text-xl font-semibold mb-2">Export</h3>
              <p className="text-gray-400">
                Download your data in your preferred format and put it to work.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 px-4 sm:px-6 lg:px-8 bg-gradient-to-r from-blue-900 to-purple-900">
        <div className="max-w-3xl mx-auto text-center">
          <h2 className="text-3xl font-bold mb-6">Ready to Extract Business Data?</h2>
          <p className="text-xl text-blue-100 mb-8">
            Join thousands of businesses and professionals who use our tool to gather valuable 
            business information and generate leads.
          </p>
          <Link 
            to="/register" 
            className="bg-white text-blue-900 hover:bg-blue-100 font-bold py-3 px-8 rounded-lg text-lg transition duration-300"
          >
            Get Started Now
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-8 px-4 sm:px-6 lg:px-8 bg-gray-800">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center">
          <div className="mb-4 md:mb-0">
            <p className="text-gray-400">© 2024 Business Extractor. All rights reserved.</p>
          </div>
          <div className="flex space-x-6">
            <Link to="/privacy" className="text-gray-400 hover:text-white">Privacy Policy</Link>
            <Link to="/terms" className="text-gray-400 hover:text-white">Terms of Service</Link>
            <Link to="/contact" className="text-gray-400 hover:text-white">Contact Us</Link>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage; 