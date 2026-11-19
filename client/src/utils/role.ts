import type { LoginSuccessResponse } from "../features/auth/types/auth.types";

export const hasAnyRole = (
  auth: LoginSuccessResponse | null,
  roles: string[]
): boolean => {

  if (!auth) {
    return false;
  }

  return roles.some((role) =>
    (auth.roles ?? []).includes(role)
  );
};