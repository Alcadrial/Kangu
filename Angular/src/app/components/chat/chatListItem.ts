import { Component, inject, input } from "@angular/core";
import { PastTimePipe } from "~pipes/PastTimePipe";
import AuthService from "~services/auth.service";
import { ChatPreview } from "~services/chat.service";

@Component({
    selector: "chat-list-item",
    imports: [PastTimePipe],
    templateUrl: "./chatListItem.html"
}) 
export class ChatListItem {

    authService = inject(AuthService);

    chatPreview = input.required<ChatPreview>();

}