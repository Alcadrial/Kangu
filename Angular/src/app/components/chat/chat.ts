import { Component, input, model, output, signal } from "@angular/core";
import { form, FormField } from "@angular/forms/signals";
import Separator from "~components/separator";
import { ChatDetailsAndMessages } from "~services/ChatService";

@Component({
    selector: "chat-content",
    imports: [Separator, FormField],
    templateUrl: "./chat.html"
})
export class ChatContent {

    chat = input.required<ChatDetailsAndMessages>();
    isLoadingMessages = input.required<boolean>();
    isSendingMessage = input.required<boolean>();

    messageDraft = model.required<string>();
    sendMessage = output<string>();

    messageForm = form(this.messageDraft);

    onSubmit(e: Event) {
        e.preventDefault();
        if (this.isLoadingMessages()) return;
        if (this.isSendingMessage()) return;
        this.sendMessage.emit(this.messageDraft());
    }
}
