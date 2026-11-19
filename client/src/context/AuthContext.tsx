import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from "react";

import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import axiosInstance from "../services/api/axiosInstance";
import type {
  LoginSuccessResponse,
} from "../features/auth/types/auth.types";
import {
  saveAuthSession,
  getAuthSession,
  clearAuthSession,
} from "../utils/storage";

interface AuthContextType {
  auth: LoginSuccessResponse | null;
  isAuthenticated: boolean;
  login: (data: LoginSuccessResponse) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const navigate = useNavigate();

  // Restore existing authenticated session on application startup.
  const [auth, setAuth] = useState<LoginSuccessResponse | null>(
    () => getAuthSession()
  );

  // Establish authenticated session after successful login or MFA.
  const login = useCallback((data: LoginSuccessResponse) => {
    if (!data?.accessToken) {
      throw new Error("Valid authentication token is required.");
    }

    saveAuthSession(data);
    setAuth(data);
  }, []);

  // Clear authenticated session.
  const logout = useCallback(() => {
    clearAuthSession();
    setAuth(null);

    navigate("/login", { replace: true });
  }, [navigate]);

  // Configure Axios interceptors.
  useEffect(() => {
    // Request interceptor.
    const requestInterceptor = axiosInstance.interceptors.request.use((config) => {
      const session = getAuthSession();
      
      if (session && "accessToken" in session && session.accessToken) {
        config.headers.Authorization = `${session.tokenType} ${session.accessToken}`;
      }

      return config;
    });

    // Response interceptor.
    const responseInterceptor =
      axiosInstance.interceptors.response.use(
        (response) => response,

        (error: unknown) => {
          if (
            axios.isAxiosError(error) &&
            error.response?.status === 401
          ) {
            const requestUrl = error.config?.url ?? "";

            // Public authentication endpoints should not trigger logout.
            const publicAuthEndpoints = [
              "/api/v1/auth/login",
              "/api/v1/auth/verify-mfa",
            ];

            const isPublicAuthRequest = publicAuthEndpoints.some(
              (endpoint) => requestUrl.includes(endpoint)
            );

            if (!isPublicAuthRequest && getAuthSession()) {
              logout();
            }
          }

          return Promise.reject(error);
        }
      );

    // Remove interceptors when provider unmounts.
    return () => {
      axiosInstance.interceptors.request.eject(
        requestInterceptor
      );

      axiosInstance.interceptors.response.eject(
        responseInterceptor
      );
    };
  }, [logout]);

  // Context value.
  const value = useMemo<AuthContextType>(
    () => ({
      auth,
      isAuthenticated: Boolean(auth?.accessToken),
      login,
      logout,
    }),
    [auth, login, logout]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

