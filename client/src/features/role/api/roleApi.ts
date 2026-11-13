
import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Role,
  RoleListResponse,
  CreateRoleRequest,
  UpdateRolePermissionsRequest,
} from "../types/role.types";

export const getAllRoles = async (): Promise<RoleListResponse> => {
  const response = await axiosInstance.get<Role[]>(
    "/api/v1/roles"
  );

  return response.data;
};

// export const createRole = async (
//   request: CreateRoleRequest
// ): Promise<Role> => {
//   const response = await axiosInstance.post<Role>(
//     "/api/v1/roles",
//     request
//   );

//   return response.data;
// };



export const createRole = async (
  request: CreateRoleRequest
): Promise<Role | null> => {

  const response = await axiosInstance.post<Role | null>(
    "/api/v1/roles",
    request
  );

  return response.data ?? null;
};


// Assign or update permissions for a role
export const updateRolePermissions = async (
  roleId: number,
  request: UpdateRolePermissionsRequest
): Promise<void> => {
  await axiosInstance.put(
    `/api/v1/roles/${roleId}/permissions`,
    request
  );
};


export const deactivateRole = async (
  roleId: number
): Promise<void> => {

  await axiosInstance.patch(
    `/api/v1/roles/${roleId}/deactivate`
  );

};
