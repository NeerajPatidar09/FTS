
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

import type { LoginResponse } from "../features/auth/types/auth.types";

import {
  saveAuthSession,
  getAuthSession,
  clearAuthSession,
} from "../utils/storage";

interface AuthContextType {
  auth: LoginResponse | null;
  isAuthenticated: boolean;
  login: (data: LoginResponse) => void;
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

  // Restore existing session when application starts
  const [auth, setAuth] = useState<LoginResponse | null>(
    () => getAuthSession()
  );

  // Login function
  const login = useCallback((data: LoginResponse) => {

    saveAuthSession(data);

    setAuth(data);

  }, []);

  // Logout function
  const logout = useCallback(() => {

    clearAuthSession();

    setAuth(null);

    navigate("/login", { replace: true });

  }, [navigate]);

  // Configure Axios interceptors
  useEffect(() => {

    // Request interceptor
    const requestInterceptor =
      axiosInstance.interceptors.request.use((config) => {

        const session = getAuthSession();

        if (session?.accessToken) {

          config.headers.Authorization =
            `${session.tokenType} ${session.accessToken}`;

        }

        return config;
      });

    // Response interceptor
    const responseInterceptor =
      axiosInstance.interceptors.response.use(

        (response) => response,

        (error: unknown) => {

          if (
            axios.isAxiosError(error) &&
            error.response?.status === 401
          ) {
            logout();
          }

          return Promise.reject(error);
        }

      );

    // Cleanup interceptors when provider unmounts
    return () => {

      axiosInstance.interceptors.request.eject(
        requestInterceptor
      );

      axiosInstance.interceptors.response.eject(
        responseInterceptor
      );

    };

  }, [logout]);

  // Authentication state
  const value = useMemo(() => ({

    auth,

    isAuthenticated: Boolean(auth?.accessToken),

    login,

    logout,

  }), [auth, login, logout]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
