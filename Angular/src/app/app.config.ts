import { ApplicationConfig, provideAppInitializer, provideBrowserGlobalErrorListeners } from "@angular/core";
import { provideRouter, withComponentInputBinding, withRouterConfig } from "@angular/router";
import { routes } from "./app.routes";
import { provideClientHydration, withEventReplay } from "@angular/platform-browser";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { GlobalErrorHandler } from "./errorHandler";
import { errorInterceptor, xsrfInterceptor } from "./interceptors";

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes, withComponentInputBinding()),
        provideClientHydration(withEventReplay()),
        provideHttpClient(withInterceptors([xsrfInterceptor, errorInterceptor])),
        provideAppInitializer(() => {}),
        GlobalErrorHandler,
    ]
};
