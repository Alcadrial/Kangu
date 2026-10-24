import { Component, computed, inject, signal } from "@angular/core";
import { email, form, submit, maxLength, minLength, required } from "@angular/forms/signals";
import { RouterLink } from "@angular/router";
import { countRegex } from "../../utils/strings";
import AuthService from "../../service/AuthService";


@Component({
    selector: "account-root",
    imports: [RouterLink],
    templateUrl: "./account.html"
})
export class Account {

    authService = inject(AuthService);

    username = signal("Anonymous");

}
