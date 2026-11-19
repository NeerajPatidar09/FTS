import type { LoginSuccessResponse } from "../features/auth/types/auth.types";

// Check whether the user has a particular permission
export const hasPermission = (
  auth: LoginSuccessResponse | null,
  permission: string
): boolean => {

  if (!auth) {
    return false;
  }

  return (auth?.permissions ?? []).includes(permission);
};

// Check whether the user has at least one permission
export const hasAnyPermission = (
  auth: LoginSuccessResponse | null,
  permissions: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return permissions.some((permission) =>
    (auth?.permissions ?? []).includes(permission)
  );
};

// Check whether the user has all required permissions
export const hasAllPermissions = (
  auth: LoginSuccessResponse | null,
  permissions: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return permissions.every((permission) =>
    (auth?.permissions ?? []).includes(permission)
  );
};

// Check user role
export const hasRole = (
  auth: LoginSuccessResponse | null,
  role: string
): boolean => {

  if (!auth) {
    return false;
  }

  return (auth?.roles ?? []).includes(role);
};
