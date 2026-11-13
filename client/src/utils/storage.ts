
import type { LoginResponse } from "../features/auth/types/auth.types";

const AUTH_STORAGE_KEY = "Stranger_auth";

interface StoredAuthSession {
  data: LoginResponse;
  expiresAt: number;
}

export const saveAuthSession = (data: LoginResponse): void => {
  const expiresAt = Date.now() + data.expiresIn;

  const session: StoredAuthSession = {
    data,
    expiresAt,
  };

  sessionStorage.setItem(
    AUTH_STORAGE_KEY,
    JSON.stringify(session)
  );
};

export const getAuthSession = (): LoginResponse | null => {
  const storedData = sessionStorage.getItem(AUTH_STORAGE_KEY);

  if (!storedData) {
    return null;
  }

  try {
    const session: StoredAuthSession = JSON.parse(storedData);

    // Check token expiration
    if (!session.expiresAt || Date.now() >= session.expiresAt) {
      clearAuthSession();
      return null;
    }

    return session.data;

  } catch {
    clearAuthSession();
    return null;
  }
};

export const clearAuthSession = (): void => {
  sessionStorage.removeItem(AUTH_STORAGE_KEY);
};
