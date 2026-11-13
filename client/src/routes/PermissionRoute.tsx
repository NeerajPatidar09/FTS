
import { Navigate, Outlet } from "react-router-dom";

import { useAuth } from "../features/auth/hooks/useAuth";

import { hasAllPermissions } from "../utils/permission";

interface PermissionRouteProps {
  permissions: string[];
}

export default function PermissionRoute({
  permissions,
}: PermissionRouteProps) {

  const { auth } = useAuth();

  // Not authenticated
  if (!auth) {
    return <Navigate to="/login" replace />;
  }

  // Check required permissions
  const authorized = hasAllPermissions(auth, permissions);

  if (!authorized) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}
