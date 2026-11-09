import { Component, CUSTOM_ELEMENTS_SCHEMA, input, model, output } from "@angular/core";

import "@awesome.me/webawesome/dist/components/dialog/dialog.js";

@Component({
    selector: "app-dialog",
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    template: `
        <wa-dialog
            [attr.light-dismiss]="lightDismiss() ? '' : null"
            [label]="title()"
            [open]="open()"
            (wa-hide)="open.set(false)"
        >
            <ng-content />
        </wa-dialog>
    `
})
export default class DialogComponent {

    readonly open = model<boolean>(false);

    readonly lightDismiss = input<boolean>(true);
    readonly submitText = input<string>("Submit");
    readonly title = input<string>();

    readonly submit = output<Event>();

    //readonly backgroundClass = computed(() => `fixed z-1 size-full flex justify-center items-center ${this.forceFocus() ? "bg-surface-700/60" : "pointer-events-none"}`)
}