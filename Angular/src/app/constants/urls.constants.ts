import { API_PATHS, AUTHENTICATION_PATHS, ERROR_PATHS, PUBLIC_PATHS, USER_PATHS } from "./path.constants";

export const PUBLIC_URLS = {
    home: `/${PUBLIC_PATHS.home}`
};

export const AUTHENTICATION_URLS = {
    login: `/${API_PATHS.base}/${AUTHENTICATION_PATHS.login}`,
    register: `/${API_PATHS.base}/${AUTHENTICATION_PATHS.register}`,
    status: `/${API_PATHS.base}/${AUTHENTICATION_PATHS.sessionStatus}`,
    logout: `/${API_PATHS.base}/${AUTHENTICATION_PATHS.logout}`,
    delete: `/${API_PATHS.base}/${AUTHENTICATION_PATHS.delete}`,
};

export const USER_URLS = {
    chat: `/${API_PATHS.base}/${USER_PATHS.chat}`,
};

export const ERROR_URLS = {
    notFound: `/${ERROR_PATHS.base}?reason=notFound`,
    unknown: `/${ERROR_PATHS.base}`
};