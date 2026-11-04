import { Component, inject, signal } from "@angular/core";
import { form, submit, FormField, required } from "@angular/forms/signals";
import { Router, RouterLink } from "@angular/router";
import AuthService from "../../services/AuthService";

interface LoginData {
    username: string;
    password: string;
    rememberMe: boolean;
}

@Component({
    selector: "login-root",
    imports: [RouterLink, FormField],
    templateUrl: "./login.html"
})
export class Login {

    authService = inject(AuthService);
    router = inject(Router);

    loginData = signal<LoginData>({
        username: "",
        password: "",
        rememberMe: false
    });
    error = signal<string | null>(null);

    loginForm = form(this.loginData, p => {
        required(p.username, { message: "Missing username" });
        required(p.password, { message: "Missing password" });
    });

    async onSubmit(event: Event) {
        event.preventDefault();
        submit(this.loginForm, async () => {
            const credentials = this.loginData();
            this.authService.login(credentials.username, credentials.password, credentials.rememberMe).subscribe({
                next: () => this.router.navigate(["/chat"]),
                error: error => this.error.set(error)
            });
        });
    }
}
