import { Directive, ElementRef, inject, input, Input, OnInit, Renderer2 } from "@angular/core";


@Directive({
    selector: "[appMessage]"
})
export default class MessageModule implements OnInit {
    private readonly el = inject(ElementRef);
    readonly appMessage = input.required<"left" | "right">();

    ngOnInit(): void {
        const div = this.el.nativeElement as HTMLDivElement;
        div.classList.add(this.appMessage() == "left" ? "justify-start" : "justify-end");
    }
}