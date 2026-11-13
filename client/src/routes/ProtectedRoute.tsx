
import { Navigate, Outlet } from "react-router-dom";

import { useAuth } from "../features/auth/hooks/useAuth";

export default function ProtectedRoute() {

  const { isAuthenticated } = useAuth();

  // User is not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // User is authenticated
  return <Outlet />;
}
