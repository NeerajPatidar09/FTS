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

// export interface LoginResponse {
//   accessToken: string;
//   tokenType: string;
//   expiresIn: number;
//   user: AuthUser;
//   menus: MenuResponse[];
// }

// new functionally 

export interface LoginRequest {
  username: string;
  password: string;
}

export interface MfaRequiredResponse {
  mfaRequired: true;
  challengeToken: string;
  expiresIn: number;
  message: string;
}

export interface LoginSuccessResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: AuthUser;
  menus: MenuResponse[];
}

export type LoginResponse =
  | LoginSuccessResponse
  | MfaRequiredResponse;