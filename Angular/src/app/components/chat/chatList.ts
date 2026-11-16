import { Component, effect, inject, input, output, signal } from "@angular/core";
import { ChatListItem } from "./chatListItem";
import { form, FormField, required } from "@angular/forms/signals";
import { Router } from "@angular/router";
import { ChatPreview } from "~services/chat.service";
import { TextInputComponent } from "~components/input";
import { DialogComponent } from "~components/dialog";
import SpinnerComponent from "~components/spinner";
import { LucideLogOut, LucidePlus } from "@lucide/angular";
import Separator from "~components/separator";
import { ChatUser } from "./chatUser";

@Component({
    selector: "chat-list",
    imports: [ChatListItem, ChatUser, TextInputComponent, Separator, DialogComponent, SpinnerComponent, FormField, 
        LucidePlus, LucideLogOut],
    templateUrl: "./chatList.html"
})
export class ChatList {

    private readonly router = inject(Router);

    readonly chats = input.required<ChatPreview[] | null>();
    readonly error = input.required<string | null>();
    readonly isCreateConversationModalOpen = signal<boolean>(false);

    readonly newChatUsername = signal<string>("");

    readonly logout = output<null>();

    newChatForm = form(this.newChatUsername, username => {
        required(username);
    });

    constructor() {
        effect(() => {
            if (this.isCreateConversationModalOpen()) {
                this.newChatForm().reset();
            }
        });
    }

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

    doLogout() {
        this.logout.emit(null);
    }
}