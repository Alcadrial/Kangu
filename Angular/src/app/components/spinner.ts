import { Component, computed, CUSTOM_ELEMENTS_SCHEMA, input } from "@angular/core";
import { UiSize, UiSizeMap } from "~utils/types";

@Component({
    selector: "app-spinner",
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    template: `
        <div class="size-full flex flex-col justify-center items-center gap-1">
            <wa-spinner [style]="sizeStyle()"></wa-spinner>
            <span>{{ text() }}</span>
        </div>
    `
})
export default class SpinnerComponent {

    readonly size = input<UiSize>("md");

    readonly text = input<string>("Loading...");
    readonly sizeStyle = computed<string>(() => `font-size: ${UiSizeMap[this.size()]}rem;`);
}