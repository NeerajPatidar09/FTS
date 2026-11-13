
export interface Permission {
  id: number;
  permissionCode: string;
  permissionName: string;
  description: string;
  active: boolean;
}

export interface CreatePermissionRequest {
  permissionCode: string;
  permissionName: string;
  description: string;
}

export type PermissionFormValues = CreatePermissionRequest;

// export type PermissionListResponse = Permission[];
