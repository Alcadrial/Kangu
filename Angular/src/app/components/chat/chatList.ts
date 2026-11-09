import { Component, CUSTOM_ELEMENTS_SCHEMA, inject, input, signal } from "@angular/core";
import { ChatListItem } from "./chatListItem";
import { form, FormField, required } from "@angular/forms/signals";
import { Router } from "@angular/router";
import { Details } from "~services/chat.service";
import { TextInputComponent } from "~components/input";
import DialogComponent from "~components/dialog";
import SpinnerComponent from "~components/spinner";

@Component({
    selector: "chat-list",
    imports: [ChatListItem, TextInputComponent, DialogComponent, SpinnerComponent, FormField],
    templateUrl: "./chatList.html",
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ChatList {

    private readonly router = inject(Router);

    readonly chats = input.required<Details[] | null>();
    readonly isCreateConversationModalOpen = signal<boolean>(false);

    readonly newChatUsername = signal<string>("");

    newChatForm = form(this.newChatUsername, username => {
        required(username);
    });

    openChat(username: string) {
        this.router.navigate(["/chat", username]);
        this.isCreateConversationModalOpen.set(false);
        this.newChatUsername.set("");
    }

    onCloseDialog() {
        this.newChatUsername.set("");
    }

    createNewConversation(e: Event) {
        e.preventDefault();
        if (this.newChatForm().invalid()) return;

        this.openChat(this.newChatUsername());
    }
}