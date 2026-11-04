import { ApplicationConfig, provideAppInitializer, provideBrowserGlobalErrorListeners } from "@angular/core";
import { provideRouter } from "@angular/router";
import { routes } from "./app.routes";
import { provideClientHydration, withEventReplay } from "@angular/platform-browser";
import { provideHttpClient, withFetch, withInterceptors } from "@angular/common/http";
import { GlobalErrorHandler } from "./errorHandler";
import { errorInterceptor, xsrfInterceptor } from "./interceptors";

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes),
        provideClientHydration(withEventReplay()),
        provideHttpClient(withFetch(), withInterceptors([xsrfInterceptor, errorInterceptor])),
        provideAppInitializer(() => {}),
        GlobalErrorHandler,
    ]
};
