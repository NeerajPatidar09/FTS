import type { LoginResponse } from "../features/auth/types/auth.user.types";

export const hasAnyRole = (
  auth: LoginResponse | null,
  roles: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return roles.some((role) =>
    (auth.user.roles ?? []).includes(role)
  );
};