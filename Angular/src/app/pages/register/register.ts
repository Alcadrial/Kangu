import { Component, computed, inject, signal } from "@angular/core";
import { form, submit, FormField, maxLength, minLength, required, pattern } from "@angular/forms/signals";
import { Router, RouterLink } from "@angular/router";
import AuthService from "~services/auth.service";
import { countRegex } from "~utils/strings";

interface FormData {
    username: string;
    email: string;
    password: string;
}

@Component({
    selector: "register-root",
    imports: [RouterLink, FormField],
    templateUrl: "./register.html"
})
export class Register {

    authService = inject(AuthService);
    router = inject(Router);

    formData = signal<FormData>({
        username: "",
        email: "",
        password: ""
    });
    secureLevel = computed(() => {

        const password = this.formData().password;
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

    form = form(this.formData, p => {
        required(p.username, { message: "Missing username" });
        pattern(p.username, /^[a-zA-Z0-9_-]+$/, { message: "Only letters, numbers, '-' and '_' can be used in the username" });
        required(p.email, { message: "Missing email" });
        required(p.password, { message: "Missing password" });
        minLength(p.password, 8, { message: "Password must be at least 8 characters long" });
        maxLength(p.password, 32, { message: "Password must be maximun 32 characters long" });
        pattern(p.email, /^([a-zA-Z0-9!#$%&*+=?^_`{|}~-]+(\.[A-Za-z0-9!#$%&*+=?^_`{|}~-]+)*)@(([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?)$/, { message: "Enter a valid email" });
    });

    async onSubmit(event: Event) {
        event.preventDefault();
        submit(this.form, async () => {
            const credentials = this.formData();
            this.authService.register(credentials.username, credentials.email, credentials.password, false).subscribe({
                next: () => this.router.navigate(["/chat"]),
                error: error => this.error.set(error)
            });
        });
    }
}
