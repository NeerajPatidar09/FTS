import type { LoginResponse } from "../features/auth/types/auth.types";

export const hasAnyRole = (
  auth: LoginResponse | null,
  roles: string[]
): boolean => {

  if (!auth) {
    return false;
  }
  if (!("user" in auth)) {
    return false;
  }
  return roles.some((role) =>
    (auth.user.roles ?? []).includes(role)
  );
};