
import { Navigate, Outlet } from "react-router-dom";

import { useAuth } from "../features/auth/hooks/useAuth";

import { hasAnyRole } from "../utils/role";

interface RoleRouteProps {
  roles: string[];
}

export default function RoleRoute({ roles }: RoleRouteProps) {

  const { auth } = useAuth();

  if (!auth) {
    return <Navigate to="/login" replace />;
  }

  if (!hasAnyRole(auth, roles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}
