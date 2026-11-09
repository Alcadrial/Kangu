import { translations } from "../translations";

export const HTTP_ERRORS = [
    { status: 502, error: translations.serverConnectionFail },
    { status: 500, error: translations.internalServerError },
    { status: 499, error: translations.unknownError },
];

export const authData = {
    principal: "user",
    isEnabled: true,
    isAnonymous: false,
    isExpired: false,
    isLocked: false,
    authorities: ["ROLE_USER"],
}

export const userData = {
    username: "user",
    email: "g@g.g",
    isAdmin: false,
    registrationTime: new Date(),
    lastAccessTime: new Date(),
    lastActionTime: new Date(),
    onlineStatus: "OFF"
};