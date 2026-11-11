import { CommonModule } from "@angular/common";
import { Component, input, model } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { FieldTree, FormField, FormValueControl } from "@angular/forms/signals";

@Component({
    selector: "app-input",
    imports: [CommonModule, ReactiveFormsModule, FormField],
    template: `
        @let formField = control();
        <div class="group justify-items-end">
            <label class="w-full flex flex-row items-center justify-items-start">
                <span class="flex-1">{{ label() }}: </span>
                <input
                    class="w-58 px-4 py-1 rounded-none peer focus:outline-0"
                    [type]="type()"
                    [placeholder]="placeholder()"
                    [autofocus]="autofocus()"
                    [formField]="formField"
                />
            </label>
            @let hasError = formField().invalid() && formField().touched();
            <div [className]="hasError ?
                'h-[1px] mx-0 bg-red-600 w-58 group-has-focus:w-full transition-all duration-150 ease-out' :
                'h-[1px] mx-3 bg-amber-600 w-52 group-has-focus:w-full group-has-focus:m-0 transition-all duration-150 ease-out'
            "></div>
            <div class="w-58 h-6 flex flex-row gap-4">
                @if (hasError) {
                    @for (error of formField().errors(); track error) {
                        <span class="w-58 px-4 py-[2px] text-red-600 text-sm">{{ error.message }}</span>
                    }
                }
            </div>
        </div>
  `,
})
export class TextInputComponent implements FormValueControl<string> {

    readonly value = model("");

    readonly label = input<string>("");
    readonly type = input<string>("text");
    readonly placeholder = input<string>("");
    readonly autofocus = input<"true" | "false" | "" | boolean>(false);

    readonly control = model.required<FieldTree<string>>({ alias: "formField" });
}