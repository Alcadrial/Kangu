import { Component, effect, input, model, output } from "@angular/core";
import { LucideX } from "@lucide/angular";
import Separator from "./separator";

@Component({
    selector: "app-dialog",
    imports: [LucideX, Separator],
    template: `
        @if (open()) {
            <div
                class="fixed z-10 inset-0 flex justify-center items-center bg-surface-800/50"
                (click)="onBackdropClick($event)"
            >
                <div class="relative max-w-[60%] max-h-[50%] bg-surface-800 border border-surface-400 rounded-sm" (click)="$event.stopPropagation()">
                    <header class="py-3 px-4 flex flex-row justify-between items-center">
                        <h2 class="text-xl">{{ title() }}</h2>
                        <button
                            class="p-1 border-none rounded-full cursor-pointer hover:bg-surface-700"
                            title="Close this dialog"
                            (click)="close($event)"
                        >
                            <svg lucideX size=20></svg>
                        </button>
                    </header>
                    <separator w="w-[95%]" color="bg-surface-600"></separator>
                    
                    <section class="p-4">
                        <ng-content></ng-content>
                    </section>
                </div>
            </div>
        }
    `
})
export class DialogComponent {

    readonly open = model<boolean>(false);

    readonly lightDismiss = input<boolean>(true);
    readonly submitText = input<string>("Submit");
    readonly title = input<string>();

    readonly submit = output<Event>();

    onBackdropClick(e: Event) {
        if (this.lightDismiss()) {
            this.open.set(false);
        }
    }

    close(e: Event) {
        e.preventDefault();
        this.open.set(false);
    }
}