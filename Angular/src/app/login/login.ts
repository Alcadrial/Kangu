import { Component, computed, inject, signal } from "@angular/core";
import { email, form, submit, FormField, maxLength, minLength, required } from "@angular/forms/signals";
import { Router, RouterLink } from "@angular/router";
import { countRegex } from "../../utils/strings";
import { HttpClient } from "@angular/common/http";
import AuthService from "../../service/AuthService";

interface LoginData {
    username: string;
    email: string;
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
        email: "",
        password: "",
        rememberMe: false
    });
    secureLevel = computed(() => {

        const password = this.loginData().password;
        if (password.length == 0) return null;

        let score = 0;
        score += countRegex(password, /[A-Z]/, 2);
        score += countRegex(password, /[0-9]/, 4);
        score += 2 * countRegex(password, / /, 2);
        score += 3 * countRegex(password, /[^a-zA-Z0-9 ]/, 4);
        score += Math.floor(password.length / 5);

        if (score < 5) return { text: "weak", "style": "text-red-400" };
        if (score < 10) return { text: "good", "style": "text-yellow-400" };
        return { text: "strong", "style": "text-green-400" };
    });
    error = signal<string | null>(null);

    loginForm = form(this.loginData, p => {
        required(p.username, { message: "Missing username" });
        required(p.email, { message: "Missing email" });
        required(p.password, { message: "Missing password" });
        minLength(p.password, 8, { message: "Password must be at least 8 characters long" });
        maxLength(p.password, 32, { message: "Password must be maximun 32 characters long" });
        email(p.email, { message: "Enter a valid email" });
    });

    async onSubmit(event: Event) {
        event.preventDefault();
        submit(this.loginForm, async () => {
            const credentials = this.loginData();
            try {
                if (await this.authService.login(credentials.username, credentials.password)) {
                    this.router.navigate(["account"]);
                }
                else {
                    this.error.set("Wrong username or password");
                }
            }
            catch (error) {
                this.error.set((error as any).toString());
            }
        });
    }
}
