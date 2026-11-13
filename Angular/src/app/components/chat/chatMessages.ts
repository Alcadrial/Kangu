import { Component, effect, ElementRef, input, linkedSignal, output, signal, viewChild } from "@angular/core";
import { form, FormField } from "@angular/forms/signals";
import Separator from "~components/separator";
import SpinnerComponent from "~components/spinner";
import { ChatPreviewAndMessages } from "~services/chat.service";
import MessageModule from "~directives/MessageDirective";
import { LucideEllipsisVertical, LucideMoveDown } from "@lucide/angular";
import { SameDayPipe } from "~pipes/ShortDatePipe";
import { DatePipe } from "@angular/common";

@Component({
    selector: "chat-messages",
    imports: [Separator, FormField, SpinnerComponent, SameDayPipe, DatePipe, MessageModule, 
        LucideMoveDown, LucideEllipsisVertical],
    templateUrl: "./chatMessages.html"
})
export class ChatMessages {

    readonly chat = input.required<ChatPreviewAndMessages | null>();
    readonly isLoadingMessages = input.required<boolean>();
    readonly isSendingMessage = input.required<boolean>();

    readonly messageDraft = input.required<string>();

    private readonly message = linkedSignal(() => this.messageDraft());
    readonly isNearBottom = signal<boolean>(false);
    readonly isAtTop = signal<boolean>(false);
    readonly isAtBottom = signal<boolean>(true);

    readonly sendMessage = output<string>();

    readonly contentRef = viewChild<ElementRef<HTMLDivElement>>("content");
    readonly containerRef = viewChild<ElementRef<HTMLDivElement>>("container");

    messageForm = form(this.message);

    constructor() {
        effect(() => {
            const content = this.contentRef()?.nativeElement;
            if (content) {
                const handle = () => {
                    const s = content.scrollHeight - content.scrollTop;
                    this.isNearBottom.set(s < 1.5 * content.clientHeight);
                    this.isAtTop.set(content.scrollTop < 10);
                    this.isAtBottom.set(s < 10 + content.clientHeight);
                };
                content.addEventListener("scroll", handle);
                handle();
                return () => {
                    content.removeEventListener("scroll", handle);
                };
            }
            return () => {};
        });
        effect(() => {
            //on chat updates
            this.chat();
            if (this.isAtBottom()) {
                setTimeout(() => {
            this.scrollToBottom();
        });
    }
        });
    }

    scrollToBottom() {
        const content = this.contentRef()?.nativeElement;
        if (content) {
            content.scrollTop = content.scrollHeight;
        }
    }

    openUserMenu(username: string) {
        
    }

    onSubmit(e: Event) {
        e.preventDefault();
        if (this.isLoadingMessages()) return;
        if (this.isSendingMessage()) return;
        this.sendMessage.emit(this.message());
    }
}
