
import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Permission,
  PermissionListResponse,
  CreatePermissionRequest,
} from "../types/permission.types";

export const getAllPermissions =
  async (): Promise<PermissionListResponse> => {

    const response = await axiosInstance.get<Permission[]>(
      "/api/v1/permissions"
    );

    return response.data;
  };

// CREATE PERMISSION
export const createPermission = async (
  request: CreatePermissionRequest
): Promise<Permission> => {

  const response = await axiosInstance.post<Permission>(
    "/api/v1/permissions",
    request
  );

  return response.data;
};