import { Component, computed, inject, input, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";

@Component({
    selector: "root",
    imports: [RouterLink],
    templateUrl: "./error.html"
})
export class Error {
    reason = signal<string | undefined>(undefined);
    description = computed(() => {
        const r = this.reason();
        return (this.errors[r ?? "unknown"]) ?? this.errors["unknown"];
    });
    activatedRoute = inject(ActivatedRoute);

    errors: { [key: string]: { title: string, message: string } | undefined } = {
        notFound: { title: "Resource not found", message: "Your page was not found in this server" },
        unauthorized: { title: "Resource access denied", message: "You are not allowed to perform this action" },
        unknown: { title: "Unknown", message: "An unknown error occurred" }
    };

    constructor() {
        this.activatedRoute.queryParams.subscribe(params => {
            this.reason.set(params["reason"]);
        });
    }
}
