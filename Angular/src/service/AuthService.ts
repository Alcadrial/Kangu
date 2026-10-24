import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom, take } from "rxjs";
import { Login } from "../app/login/login";


type LoginResponse = {
    error?: string;
}

@Injectable({ providedIn: "root", deps: [Login] })
export default class AuthService {

    http = inject(HttpClient);
    csrfToken: string | null = null;


    public login(username: string, password: string): Promise<boolean> {
        return new Promise((resolve, reject) => {
            firstValueFrom(this.http.post<LoginResponse>("/api/login", { username , password }, { redirect: "manual"})).then(() => {
                resolve(true);
            }, ({ error, status }) => {
                if (status == 307) return void resolve(true);
                if (status == 401) return void resolve(false);
                reject(error?.error ?? error ?? "unknown error");
            });
        });
    }

    public register(username: string, email: string, password: string): Promise<boolean> {
        return new Promise((resolve, reject) => {
            firstValueFrom(this.http.post<LoginResponse>("/api/register", { username, email, password }, { redirect: "manual" })).then(() => {
                resolve(true);
            }, ({ error, status }) => {
                if (status == 307) return void resolve(true);
                if (status == 401) return void resolve(false);
                reject(error?.error ?? error ?? "unknown error");
            });
        });
    }

    public queryCsrfToken(): Promise<{ csrf: string }> {
        const promise = firstValueFrom(this.http.get<{ csrf: string }>("/api/csrf").pipe(take(1)));
        promise.then(({ csrf }) => this.csrfToken = csrf);
        return promise;
    }
}