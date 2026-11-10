import { Component, effect, ElementRef, input, linkedSignal, output, viewChild } from "@angular/core";
import { form, FormField } from "@angular/forms/signals";
import Separator from "~components/separator";
import SpinnerComponent from "~components/spinner";
import { ChatPreviewAndMessages } from "~services/chat.service";
import { DatePipe } from "@angular/common";
import MessageModule from "~directives/MessageDirective";

@Component({
    selector: "chat-messages",
    imports: [Separator, FormField, SpinnerComponent, DatePipe, MessageModule],
    templateUrl: "./chatMessages.html"
})
export class ChatMessages {

    readonly chat = input.required<ChatPreviewAndMessages | null>();
    readonly isLoadingMessages = input.required<boolean>();
    readonly isSendingMessage = input.required<boolean>();

    readonly messageDraft = input.required<string>();
    private readonly message = linkedSignal(() => this.messageDraft());
    readonly sendMessage = output<string>();

    readonly divRef = viewChild<ElementRef<HTMLDivElement>>("content");

    messageForm = form(this.message);

    constructor() {
        effect(() => {
            const content = this.divRef()?.nativeElement;
            if (content) {
                content.scrollTop = content.scrollHeight;
            }
        })
    }

    onSubmit(e: Event) {
        e.preventDefault();
        if (this.isLoadingMessages()) return;
        if (this.isSendingMessage()) return;
        this.sendMessage.emit(this.message());
    }
}
