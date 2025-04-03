import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { authAPI } from '../services/api';

/**
 * Email verification component.
 * This component handles two scenarios:
 * 1. Showing a page after registration informing the user to check their email
 * 2. Handling email verification when the user clicks the link in their email
 */
const EmailVerify: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [verifying, setVerifying] = useState(false);
  const [verified, setVerified] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  useEffect(() => {
    // Extract token from URL if present
    const params = new URLSearchParams(location.search);
    const token = params.get('token');
    
    // If token exists, attempt to verify
    if (token) {
      verifyEmail(token);
    }
  }, [location]);
  
  const verifyEmail = async (token: string) => {
    setVerifying(true);
    setError(null);
    
    try {
      const response = await authAPI.verifyEmail(token);
      
      if (response.status === 200) {
        setVerified(true);
        
        // Update user in local storage if they're logged in
        const userStr = localStorage.getItem('user');
        if (userStr) {
          const user = JSON.parse(userStr);
          user.emailVerified = true;
          localStorage.setItem('user', JSON.stringify(user));
        }
        
        // After 3 seconds, redirect to login or dashboard
        setTimeout(() => {
          if (localStorage.getItem('token')) {
            navigate('/dashboard');
          } else {
            navigate('/login');
          }
        }, 3000);
      }
    } catch (err: any) {
      console.error('Verification error:', err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('An error occurred during verification. Please try again or contact support.');
      }
    } finally {
      setVerifying(false);
    }
  };
  
  // Check if token is in URL
  const isVerificationPage = location.search.includes('token=');
  
  return (
    <div className="min-h-screen bg-gray-900 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        {isVerificationPage ? (
          // Email verification in progress
          <div className="text-center">
            <h2 className="mt-6 text-3xl font-extrabold text-white">
              {verified ? 'Email Verified!' : 'Verifying Your Email'}
            </h2>
            
            {verifying && (
              <div className="mt-4">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
                <p className="mt-4 text-lg text-gray-300">Please wait while we verify your email...</p>
              </div>
            )}
            
            {verified && (
              <div className="mt-4">
                <div className="rounded-full h-12 w-12 bg-green-500 mx-auto flex items-center justify-center">
                  <svg className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <p className="mt-4 text-lg text-gray-300">Your email has been successfully verified!</p>
                <p className="mt-2 text-sm text-gray-400">You will be redirected shortly...</p>
              </div>
            )}
            
            {error && (
              <div className="mt-4">
                <div className="rounded-full h-12 w-12 bg-red-500 mx-auto flex items-center justify-center">
                  <svg className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </div>
                <p className="mt-4 text-lg text-red-400">{error}</p>
                <div className="mt-4">
                  <Link 
                    to="/login" 
                    className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    Return to Login
                  </Link>
                </div>
              </div>
            )}
          </div>
        ) : (
          // After registration, telling user to check email
          <div className="text-center">
            <div className="rounded-full h-16 w-16 bg-blue-600 mx-auto flex items-center justify-center">
              <svg className="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            
            <h2 className="mt-6 text-3xl font-extrabold text-white">
              Check Your Email
            </h2>
            
            <div className="mt-4 text-md text-gray-300">
              <p>We've sent a verification link to your email address.</p>
              <p className="mt-2">Please check your inbox and click the link to verify your account.</p>
            </div>
            
            <div className="mt-8 bg-gray-800 rounded-lg p-6 mx-auto max-w-md">
              <h3 className="text-lg font-medium text-white">Didn't receive the email?</h3>
              <ul className="mt-4 text-sm text-gray-400 space-y-3">
                <li className="flex items-start">
                  <svg className="h-5 w-5 text-gray-500 mr-2 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Check your spam or junk folder
                </li>
                <li className="flex items-start">
                  <svg className="h-5 w-5 text-gray-500 mr-2 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Ensure you entered the correct email address
                </li>
                <li className="flex items-start">
                  <svg className="h-5 w-5 text-gray-500 mr-2 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  Allow a few minutes for the email to arrive
                </li>
              </ul>
              
              <div className="mt-6">
                <button
                  type="button"
                  className="w-full inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  Resend Verification Email
                </button>
              </div>
            </div>
            
            <div className="mt-8">
              <Link 
                to="/dashboard" 
                className="text-sm font-medium text-blue-500 hover:text-blue-400"
              >
                Continue to dashboard without verifying
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default EmailVerify; 