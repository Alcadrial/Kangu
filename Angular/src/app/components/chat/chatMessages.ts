import { Component, computed, CUSTOM_ELEMENTS_SCHEMA, input, linkedSignal, model, output, signal } from "@angular/core";
import { form, FormField } from "@angular/forms/signals";
import Separator from "~components/separator";
import SpinnerComponent from "~components/spinner";
import { ChatDetailsAndMessages } from "~services/chat.service";

@Component({
    selector: "chat-messages",
    imports: [Separator, FormField, SpinnerComponent],
    templateUrl: "./chatMessages.html"
})
export class ChatMessages {

    readonly chat = input.required<ChatDetailsAndMessages | null>();
    readonly isLoadingMessages = input.required<boolean>();
    readonly isSendingMessage = input.required<boolean>();

    readonly messageDraft = input.required<string>();
    private readonly message = linkedSignal(() => this.messageDraft());
    readonly sendMessage = output<string>();

    messageForm = form(this.message);

    onSubmit(e: Event) {
        e.preventDefault();
        if (this.isLoadingMessages()) return;
        if (this.isSendingMessage()) return;
        this.sendMessage.emit(this.messageDraft());
    }
}
