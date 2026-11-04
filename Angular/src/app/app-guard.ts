import { CanActivateChildFn, CanActivateFn, Router } from "@angular/router";
import AuthService from "./services/AuthService";
import { inject } from "@angular/core";
import { catchError, map, of, retry } from "rxjs";

export const adminChildGuard: CanActivateChildFn = (childRoute, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    if (authService.hasRole("admin")) return true;
    return router.parseUrl("/error?reason=unauthorized");
};

export const userSpaceGuard: CanActivateChildFn = (childRoute, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    return authService.hasRole("user")
        .pipe(
            map(result => result ? true : router.parseUrl("/login")),
            retry(2),
            catchError(() => of(router.parseUrl("/login")))
        );
};

export const loginGuard: CanActivateChildFn = (childRoute, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    return authService.hasRole("user")
        .pipe(
            map(result => result ? router.parseUrl("/chat") : true),
            retry(2),
            catchError(() => of(true))
        );
};