import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from "@angular/core";
import { provideRouter } from "@angular/router";
import { routes } from "./app.routes";
import { provideClientHydration, withEventReplay } from "@angular/platform-browser";
import { HttpEvent, HttpHandlerFn, HttpRequest, provideHttpClient, withFetch, withInterceptors } from "@angular/common/http";
import AuthService from "../service/AuthService";
import { Observable } from "rxjs";
import { PlatformLocation } from "@angular/common";


export function xsrfInterceptorFn(
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    if (req.method === 'GET' || req.method === 'HEAD') {
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

    const token = inject(AuthService).csrfToken;
    const headerName = "X-CSRF-TOKEN";

    if (token != null && !req.headers.has(headerName)) {
        req = req.clone({ headers: req.headers.set(headerName, token) });
    }
    return next(req);
}


export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes),
        provideClientHydration(withEventReplay()),
        provideHttpClient(withFetch(), withInterceptors([xsrfInterceptorFn])),
        provideAppInitializer(() => {
            if (typeof window !== "undefined") {
                const authService = inject(AuthService);
                return authService.queryCsrfToken();
            }
            return new Promise<void>(resolve => resolve());
        })
    ]
};
