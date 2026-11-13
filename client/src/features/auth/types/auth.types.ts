
export interface MenuResponse {
  id: number;
  menuCode: string;
  menuName: string;
  menuPath: string | null;
  menuIcon: string;
  parentId: number | null;
  requiredPermission: string | null;
  displayOrder: number;
  active: boolean;
  children: MenuResponse[];
}

export interface AuthUser {
  userId: number;
  employeeId: number;
  username: string;
  email: string;
  employeeName: string;
  roles: string[];
  permissions: string[];
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;

  user: AuthUser;

  menus: MenuResponse[];
}
