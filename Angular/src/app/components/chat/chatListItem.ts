import { Component, inject, input } from "@angular/core";
import AuthService from "~services/AuthService";

export type ChatPreviewData = {
    username: string;
    lastMessage: string;
    lastMessageTime: Date;
}

@Component({
    selector: "chat-list-item",
    imports: [],
    templateUrl: "./chatListItem.html"
})
export class ChatListItem {

    authService = inject(AuthService);

    user = input.required<ChatPreviewData>();

}