import { Component, computed, input } from "@angular/core";

@Component({
    selector: "separator",
    imports: [],
    template: `
        <div class="flex justify-center items-center">
            <div [class]="classValue()"></div>
        </div>
    `
})
export default class Separator {
    color = input<string>();
    w = input<string>();
    h = input<string>();
    isVertical = input<boolean>();

    classValue = computed(() => {
        const isVertical = this.isVertical() ?? false;
        const color = this.color() ?? "bg-neutral-500";
        const w = this.w() ?? (isVertical ? "w-[1px]" : "w-[80%]");
        const h = this.h() ?? (isVertical ? "h-[80%]" : "h-[1px]");
        return `${w} ${h} ${color} bg-amber-500`;
    });
}
