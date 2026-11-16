import { PlatformLocation } from "@angular/common";
import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, mergeMap, Observable } from "rxjs";
import AuthService from "./services/auth.service";
import { Router } from "@angular/router";
import { AUTHENTICATION_URLS } from "./constants/urls.constants";
import AlertService from "./services/alert.service";
import { translations } from "./translations";
import { headers } from "./app.constants";

export function xsrfInterceptor(
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    if (req.method === "GET" || req.method === "HEAD") {
        return next(req);
    }

    try {
        const locationHref = inject(PlatformLocation).href;
        const { origin: locationOrigin } = new URL(locationHref);
        const { origin: requestOrigin } = new URL(req.url, locationOrigin);

        if (locationOrigin !== requestOrigin) {
            return next(req);
        }
    } catch {
        return next(req);
    }

    return inject(AuthService).getCsrfToken().pipe(mergeMap(token => {
        return next(req.clone({ headers: req.headers.set(headers.csrf, token) }));
    }));
}

export function errorInterceptor(
    request: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    const authService = inject(AuthService);
    const alertService = inject(AlertService);
    const router = inject(Router);

    return next(request).pipe(catchError((error: HttpErrorResponse) => {
        const status = error.status;
        if (status == 403) {
            if (error.url) {
                const { pathname } = new URL(error.url);
                if (pathname == AUTHENTICATION_URLS.logout) throw error;
            }
            authService.logout().subscribe();
            //alertService.notifyError(translations.unknownError);
            router.navigate([AUTHENTICATION_URLS.login]);
        }
        throw error;
    }));
}