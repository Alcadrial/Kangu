
const authenticationCode = "01";
export const AUTHENTICATION_ERRORS = {
    badCredentials: `${authenticationCode}000`,
    disabled: `${authenticationCode}001`,
    locked: `${authenticationCode}002`,
    invalid: `${authenticationCode}003`,
    expiredCredentials: `${authenticationCode}004`,
};

const authorizationsCode = "02";
export const AUTHORIZATION_ERRORS = {
    
};

const internalCode = "10";
export const INTERNAL_ERRORS = {

};

const genericCode = "20";
export const GENERIC_ERRORS = {
    disabledService: `${genericCode}001`
};

const clientCode = "21";
export const CLIENT_ERRORS = {
    duplicatedValue: `${clientCode}001`,
    valueTooBig: `${clientCode}002`,
    blockedChat: `${clientCode}003`,
};

const unknownCode = "FF";
export const UNKNOWN_ERRORS = {

};