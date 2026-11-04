import { Component, CUSTOM_ELEMENTS_SCHEMA, inject, signal } from "@angular/core";
import { ChatListItem, ChatPreviewData } from "./chatListItem";
import "@awesome.me/webawesome/dist/components/button/button.js";
import "@awesome.me/webawesome/dist/components/dialog/dialog.js";
import "@awesome.me/webawesome/dist/components/input/input.js";
import { form } from "@angular/forms/signals";
import { setValue } from "./common";
import { Router } from "@angular/router";

@Component({
    selector: "chat-list",
    imports: [ChatListItem],
    templateUrl: "./chatList.html",
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ChatList {

    private readonly router = inject(Router);

    readonly users = signal<ChatPreviewData[]>([]);
    readonly isCreateConversationModalOpen = signal<boolean>(false);

    readonly newChatUsername = signal({
        username: ""
    });

    newChatForm = form(this.newChatUsername);

    openChat(username: string) {
        console.log("OPENING " + username)
        this.router.navigate(["/chat", username]);
    }

    openCreateConversationModal() {
        this.isCreateConversationModalOpen.set(true);
    }

    closeCreateConversationModal() {
        this.isCreateConversationModalOpen.set(false);
    }

    createNewConversation(e: Event) {
        e.preventDefault();
    }

    setUsername(e: Event) {
        setValue(this.newChatUsername, e);
    }
}