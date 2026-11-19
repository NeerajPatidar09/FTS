import type { LoginSuccessResponse } from "../types/auth.types";

export type { LoginSuccessResponse };
export interface LoginRequest {
    username: string;
    password: string;
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
