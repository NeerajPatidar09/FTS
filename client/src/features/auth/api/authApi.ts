
// import axiosInstance from "../../../services/api/axiosInstance";

// import type { LoginResponse } from "../types/auth.types";

// interface LoginRequest {
//   username: string;
//   password: string;
// }

// export const loginApi = async (
//   request: LoginRequest
// ): Promise<LoginResponse> => {

//   const response = await axiosInstance.post<LoginResponse>(
//     "/api/v1/auth/login",
//     request
//   );

//   return response.data;
// };


import axiosInstance from "../../../services/api/axiosInstance";

import type {
  LoginRequest,
  LoginResponse,
  
} from "../types/auth.types";

export const loginApi = async (
  request: LoginRequest
): Promise<LoginResponse> => {
  const response = await axiosInstance.post<LoginResponse>(
    "/api/v1/auth/login",
    request
  );

  return response.data;
};
