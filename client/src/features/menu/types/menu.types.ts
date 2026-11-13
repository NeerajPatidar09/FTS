export interface Menu {
  id: number;
  menuCode: string;
  menuName: string;
  menuPath: string | null;
  menuIcon: string | null;
  parentId: number | null;
  requiredPermission: string | null;
  displayOrder: number;
  active: boolean;
  children: Menu[];
}

export interface CreateMenuRequest {
  menuCode: string;
  menuName: string;
  menuIcon: string;
  displayOrder: number;
  menuPath?: string;
  parentId?: number;
  requiredPermissionId?: number;
}

export interface AssignMenusRequest {
  menuIds: number[];
}
