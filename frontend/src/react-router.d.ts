declare module 'react-router-dom' {
  export interface Location {
    pathname: string;
    search: string;
    hash: string;
    state: any;
    key?: string;
  }

  export function useLocation(): Location;
  export function useNavigate(): (to: string, options?: { replace?: boolean; state?: any }) => void;
  export function useParams<T = Record<string, string>>(): T;
  export function useSearchParams(): [URLSearchParams, (searchParams: URLSearchParams) => void];

  export interface LinkProps {
    to: string;
    replace?: boolean;
    state?: any;
    className?: string;
    children?: React.ReactNode;
  }
  export const Link: React.FC<LinkProps>;

  export interface RouteProps {
    path: string;
    element: React.ReactNode;
  }
  export const Route: React.FC<RouteProps>;

  export interface RoutesProps {
    children: React.ReactNode;
  }
  export const Routes: React.FC<RoutesProps>;

  export interface BrowserRouterProps {
    children: React.ReactNode;
  }
  export const BrowserRouter: React.FC<BrowserRouterProps>;

  export interface NavigateProps {
    to: string;
    replace?: boolean;
    state?: any;
  }
  export const Navigate: React.FC<NavigateProps>;
} 