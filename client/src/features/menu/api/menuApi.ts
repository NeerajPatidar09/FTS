import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Menu,
  CreateMenuRequest,
  AssignMenusRequest,
} from "../types/menu.types";

// GET all menus
export const getAllMenus = async (): Promise<Menu[]> => {
  const response = await axiosInstance.get<Menu[]>(
    "/api/v1/menus"
  );

  return response.data;
};

// POST create parent or child menu
export const createMenu = async (
  request: CreateMenuRequest
): Promise<Menu | null> => {
  const response = await axiosInstance.post<Menu | null>(
    "/api/v1/menus",
    request
  );

  return response.data ?? null;
};

// PUT assign menus to role
export const assignMenusToRole = async (
  roleId: number,
  request: AssignMenusRequest
): Promise<void> => {
  await axiosInstance.put(
    `/api/v1/menus/roles/${roleId}`,
    request
  );
};

// PATCH deactivate menu
export const deactivateMenu = async (
  menuId: number
): Promise<void> => {
  await axiosInstance.patch(
    `/api/v1/menus/${menuId}/deactivate`
  );
};

export interface MenuPermission {
  id: number;
  permissionCode: string;
  permissionName: string;
  description?: string | null;
  active: boolean;
}

export const getMenuPermissions = async (): Promise<MenuPermission[]> => {
  const response = await axiosInstance.get<MenuPermission[]>(
    "/api/v1/permissions"
  );

  return response.data;
};


export interface MenuAssignableRole {
  id: number;
  roleCode: string;
  roleName: string;
  description?: string | null;
  active: boolean;
}

export const getMenuAssignableRoles = async (): Promise<MenuAssignableRole[]> => {
  const response = await axiosInstance.get<MenuAssignableRole[]>(
    "/api/v1/roles"
  );

  return response.data;
};
