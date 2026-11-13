export interface Department {
  id: number;
  departmentCode: string;
  departmentName: string;
  active: boolean;
}

export interface DepartmentRequest {
  departmentCode: string;
  departmentName: string;
}

export type DepartmentListResponse = Department[];
