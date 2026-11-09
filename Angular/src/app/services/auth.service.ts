import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable, of, shareReplay, single, tap } from "rxjs";
import { AUTHENTICATION_URLS } from "../constants/urls.constants";
import { handleHttpError } from "~utils/errors";

export type AuthData = {
    principal: string;
    isEnabled: boolean;
    isAnonymous: false;
    isExpired: boolean;
    isLocked: boolean;
    authorities: string[];
} | {
    principal: string;
    isAnonymous: true;
    authorities: string[];
}
type LoginResponse = AuthData
type RegisterResponse = AuthData;

@Injectable({ providedIn: "root" })
export default class AuthService {

    private readonly http = inject(HttpClient);
    private csrfTokenObservable: Observable<string> | null = null;
    private userDataObservable: Observable<AuthData> | null = null;

    constructor() {
        //this.load();
    }
/*
    private save() {
        sessionStorage["user"] = JSON.stringify(this.userData());
    }

    private load() {
        if (typeof sessionStorage == "undefined") return;
        const s = sessionStorage["user"];
        if (!s) return;
        const savedUser = JSON.parse(s);
        if (!savedUser || typeof savedUser != "object") return;
        if (!savedUser.username) return;
        if (!savedUser.email) return;
        if (!savedUser.authorities) return;
        if (!Array.isArray(savedUser.authorities)) return;
        for (const authority of savedUser.authorities) if (typeof authority != "string") return;
        this.userData.set({
            username: savedUser.username,
            email: savedUser.email,
            authorities: [...savedUser.authorities],
        });
    }
    */
    /**
     * Error returned as string
     */
    login(username: string, password: string, rememberMe: boolean) {
        return this.http.post<LoginResponse>(AUTHENTICATION_URLS.login, { username, password, rememberMe }).pipe(
            tap(r => {
                //TODO: rememberMe
                this.userDataObservable = of(r).pipe(shareReplay(1));
            }),
            handleHttpError()
        );
    }

    logout() {
        return this.http.post<{}>(AUTHENTICATION_URLS.logout, null, { redirect: "manual" }).pipe(
            tap(() => {
                this.userDataObservable = null;
            }),
            handleHttpError()
        );
    }

    register(username: string, email: string, password: string, rememberMe: boolean) {
        return this.http.post<RegisterResponse>(AUTHENTICATION_URLS.register, { username, email, password, rememberMe }).pipe(
            tap(r => {
                //TODO: rememberMe
                this.userDataObservable = of(r).pipe(shareReplay(1));
            }),
            handleHttpError()
        );
    }

    getCsrfToken() {
        if (!this.csrfTokenObservable) {
            this.csrfTokenObservable = this.http.get<{ csrf: string }>("/api/csrf").pipe(
                single(),
                map(({ csrf }) => csrf),
                shareReplay(1),
                handleHttpError()
            );
        }

        return this.csrfTokenObservable;
    }

    hasRole(role: string) {
        if (!this.userDataObservable) {
            this.userDataObservable = this.http.get<AuthData>("/api/auth").pipe(
                single(),
                shareReplay(1),
                handleHttpError()
            );
        }
        const auth = "ROLE_" + role.toUpperCase();
        return this.userDataObservable.pipe(
            map(data => data.authorities.some(a => a == auth))
        );
    }

    getUserData() {
        if (!this.userDataObservable) throw Error("Missing previous request for data");
        return this.userDataObservable;
    }
}