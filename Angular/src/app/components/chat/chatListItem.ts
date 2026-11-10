import { Component, inject, input } from "@angular/core";
import AuthService from "~services/auth.service";
import { ChatPreview } from "~services/chat.service";
import { PastTimePipe } from "../../pipes/pastTimePipe";

@Component({
    selector: "chat-list-item",
    imports: [PastTimePipe],
    templateUrl: "./chatListItem.html"
}) 
export class ChatListItem {

    authService = inject(AuthService);

    chatPreview = input.required<ChatPreview>();

}