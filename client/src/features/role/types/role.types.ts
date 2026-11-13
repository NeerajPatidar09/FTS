
export interface Role {
  id: number;
  roleCode: string;
  roleName: string;
  description: string;
  active: boolean;
  permissions: string[];
}

export interface CreateRoleRequest {
  roleCode: string;
  roleName: string;
  description: string;
}

export interface UpdateRolePermissionsRequest {
  permissionIds: number[];
}

export type RoleListResponse = Role[];
