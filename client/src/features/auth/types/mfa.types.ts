
export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginSuccessResponse {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
    userId: number;
    employeeId: number;
    username: string;
    email: string;
    employeeName: string;
    roles: string[];
    permissions: string[];
    menus: AuthMenu[];
}

export interface AuthMenu {
    id: number;
    menuCode: string;
    menuName: string;
    menuPath: string | null;
    menuIcon: string | null;
    parentId: number | null;
    requiredPermission: string | null;
    displayOrder: number;
    active: boolean;
    children: AuthMenu[];
}

export interface MfaRequiredResponse {
    mfaRequired: true;
    challengeToken: string;
    expiresIn: number;
    message: string;
}

export type LoginResponse =
    | LoginSuccessResponse
    | MfaRequiredResponse;

export interface VerifyMfaRequest {
    challengeToken: string;
    code: string;
}
