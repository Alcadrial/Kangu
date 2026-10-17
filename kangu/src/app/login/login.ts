import { Component, computed, inject, signal } from "@angular/core";
import { email, form, FormField, maxLength, minLength, required } from "@angular/forms/signals";
import { RouterLink } from "@angular/router";
import { countRegex } from "../../utils/strings";
import { ENV } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";

interface LoginData {
    username: string;
    email: string;
    password: string;
}

type LoginResponse = {
    error?: string;
}

@Component({
    selector: "login-root",
    imports: [RouterLink, FormField],
    templateUrl: "./login.html"
})
export class Login {

    http = inject(HttpClient);

    loginData = signal<LoginData>({
        username: "",
        email: "",
        password: ""
    });
    secureLevel = computed(() => {

        const password = this.loginData().password;
        if (password.length == 0) return null;

        let score = 0;
        score += countRegex(password, /[A-Z]/, 2);
        score += countRegex(password, /[0-9]/, 4);
        score += countRegex(password, / /, 2);
        score += countRegex(password, /[^a-zA-Z0-9 ]/, 5);
        score += Math.floor(password.length / 5);

        if (score < 5) return { text: "weak", "style": "text-red-400" };
        if (score < 10) return { text: "good", "style": "text-yellow-400" };
        return { text: "strong", "style": "text-green-400" };
    });

    loginForm = form(this.loginData, p => {
        required(p.username, { message: "Missing username" });
        required(p.email, { message: "Missing email" });
        required(p.password, { message: "Missing password" });
        minLength(p.password, 8, { message: "Password must be at least 8 characters long" });
        maxLength(p.password, 32, { message: "Password must be maximun 32 characters long" });
        email(p.email, { message: "Enter a valid email" });
    });

    async onSubmit() {
        this.http.get<{ csrf: string }>("/api/csrf").subscribe(({ csrf }) => {
            this.http.post<LoginResponse>("/api/login", this.loginData(), {
                headers: {
                    "x-csrf-token": csrf
                }
            }).subscribe(response => {

                if (response.error !== undefined) {
                    console.log("Login failed: ", response.error)
                }
                else {
                    console.log("Login successful");
                }
            });
        });
    }
}
