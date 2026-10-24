import { Routes } from "@angular/router";
import { Login } from "./login/login";
import { Home } from "./home/home";
import { Account } from "./protected/account";
import { Register } from "./register/register";

export const routes: Routes = [
    {
        path: "",
        component: Home,
    },
    {
        path: "login",
        component: Login,
    },
    {
        path: "register",
        component: Register,
    },
    {
        path: "account",
        component: Account,
    },
];
