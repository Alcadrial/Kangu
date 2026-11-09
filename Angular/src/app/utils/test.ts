import { translations } from "../translations";

export const HTTP_ERRORS = [
    { status: 502, error: translations.serverConnectionFail },
    { status: 500, error: translations.internalServerError },
];

export const authData = {
    principal: "user",
    isEnabled: true,
    isAnonymous: false,
    isExpired: false,
    isLocked: false,
    authorities: ["ROLE_USER"],
}