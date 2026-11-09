import { Routes } from "@angular/router";
import { Login } from "./pages/login/login";
import { Home } from "./pages/home/home";
import { Register } from "./pages/register/register";
import { loginGuard, userSpaceGuard } from "./app-guard";
import { Error } from "./pages/error/error";
import { AUTHENTICATION_PATHS, ERROR_PATHS, PUBLIC_PATHS, USER_PATHS } from "./constants/path.constants";

export const routes: Routes = [
    {
        path: PUBLIC_PATHS.home,
        component: Home,
    },
    {
        path: AUTHENTICATION_PATHS.login,
        component: Login,
        canActivate: [loginGuard]
    },
    {
        path: AUTHENTICATION_PATHS.register,
        component: Register,
    },
    {
        path: ERROR_PATHS.base,
        component: Error,
    },
    {
        path: USER_PATHS.chat,
        loadChildren: async () => import("./pages/chat/chat.routes").then((module) => module.routes),
        canActivate: [userSpaceGuard],
    },
    {
        path: "**",
        redirectTo: `/${ERROR_PATHS.base}?reason=notFound`
    },
];
