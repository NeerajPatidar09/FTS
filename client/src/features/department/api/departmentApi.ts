import axiosInstance from "../../../services/api/axiosInstance";

import type {
  Department,
  DepartmentRequest,
  DepartmentListResponse,
} from "../types/department.types";

// GET all departments
export const getAllDepartments =
  async (): Promise<DepartmentListResponse> => {

    const response = await axiosInstance.get<Department[]>(
      "/api/v1/departments"
    );

    return response.data;
  };

// POST create department
export const createDepartment = async (
  request: DepartmentRequest
): Promise<Department | null> => {

  const response = await axiosInstance.post<Department | null>(
    "/api/v1/departments",
    request
  );

  return response.data ?? null;
};

// PUT update department
export const updateDepartment = async (
  id: number,
  request: DepartmentRequest
): Promise<Department | null> => {

  const response = await axiosInstance.put<Department | null>(
    `/api/v1/departments/${id}`,
    request
  );

  return response.data ?? null;
};

// PATCH deactivate department
export const deactivateDepartment = async (
  id: number
): Promise<void> => {

  await axiosInstance.patch(
    `/api/v1/departments/${id}/deactivate`
  );
};
