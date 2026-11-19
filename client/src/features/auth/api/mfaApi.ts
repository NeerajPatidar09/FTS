
import axiosInstance from "../../../services/api/axiosInstance";
import type {
  LoginRequest,
  LoginResponse,
  LoginSuccessResponse,
  VerifyMfaRequest,
} from "../types/mfa.types";

export const loginWithPassword = async (
  request: LoginRequest
): Promise<LoginResponse> => {
  const response = await axiosInstance.post<LoginResponse>(
    "/api/v1/auth/login",
    request
  );

  return response.data;
};

export const verifyMfaCode = async (
  request: VerifyMfaRequest
): Promise<LoginSuccessResponse> => {
  const response = await axiosInstance.post<LoginSuccessResponse>(
    "/api/v1/auth/verify-mfa",
    request
  );

  return response.data;
};
