import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Account,
  CreateAccountRequest,
} from "../types/account.types";

// GET ALL ACCOUNTS
export const getAccounts = async (
  signal?: AbortSignal
): Promise<Account[]> => {

  const response = await axiosInstance.get<Account[]>(
    "/api/v1/accounts",
    { signal }
  );

  return response.data;
};

// CREATE ACCOUNT
export const createAccount = async (
  request: CreateAccountRequest
): Promise<Account> => {

  const response = await axiosInstance.post<Account>(
    "/api/v1/accounts",
    request
  );

  return response.data;
};

// GET ACCOUNT BY ACCOUNT REFERENCE
export const getAccountByRef = async (
  accountRef: string,
  signal?: AbortSignal
): Promise<Account> => {

  const response = await axiosInstance.get<Account>(
    `/api/v1/account/${encodeURIComponent(accountRef)}`,
    { signal }
  );

  return response.data;
};
