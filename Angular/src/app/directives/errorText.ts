import { Directive, HostBinding } from "@angular/core";

@Directive({
    selector: "span[waErrorMessage], p[waErrorMessage]",
    standalone: true,
    host: {
        role: "alert"
    }
})
export class WaErrorMessageDirective {
    @HostBinding("class.wa-error-message") errorClass = true;
}