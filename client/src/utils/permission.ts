
import type { LoginResponse } from "../features/auth/types/auth.user.types";

// Check whether the user has a particular permission
export const hasPermission = (
  auth: LoginResponse | null,
  permission: string
): boolean => {

  if (!auth) {
    return false;
  }

  return (auth.user.permissions ?? []).includes(permission);
};

// Check whether the user has at least one permission
export const hasAnyPermission = (
  auth: LoginResponse | null,
  permissions: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return permissions.some((permission) =>
    (auth.user.permissions ?? []).includes(permission)
  );
};

// Check whether the user has all required permissions
export const hasAllPermissions = (
  auth: LoginResponse | null,
  permissions: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return permissions.every((permission) =>
    (auth.user.permissions ?? []).includes(permission)
  );
};

// Check user role
export const hasRole = (
  auth: LoginResponse | null,
  role: string
): boolean => {

  if (!auth) {
    return false;
  }

  return (auth.user.roles ?? []).includes(role);
};
