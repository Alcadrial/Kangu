import { Component, signal } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: "home-root",
    imports: [RouterLink],
    templateUrl: "./home.html"
})
export class Home {
    protected readonly title = signal("kangu");
}
