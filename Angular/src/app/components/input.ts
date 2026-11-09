import { Component, CUSTOM_ELEMENTS_SCHEMA, input, model, output } from "@angular/core";
import { FormValueControl, ValidationError } from "@angular/forms/signals";

import "@awesome.me/webawesome/dist/components/input/input.js";

@Component({
    selector: "app-input",
    standalone: true,
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    template: `
        <wa-input
            [label]="label()"
            [type]="type()"
            [placeholder]="placeholder()"
            [value]="value()"
            [disabled]="disabled()"
            [readonly]="readonly()"
            [autofocus]="autofocus()"
            [attr.aria-invalid]="invalid()"
            (input)="value.set($any($event.target).value)"
            (blur)="touch.emit()"
        ></wa-input>

        @if (invalid()) {
            @for (error of errors(); track error) {
                <div role="alert">{{ error.message }}</div>
            }
        }
  `,
})
export class TextInputComponent implements FormValueControl<string> {
    value = model("");

    label = input("");
    type = input("text");
    placeholder = input("");

    disabled = input(false);
    readonly = input(false);
    autofocus = input(false);
    invalid = input(false);
    errors = input<readonly ValidationError[]>([]);

    touch = output<void>();
}