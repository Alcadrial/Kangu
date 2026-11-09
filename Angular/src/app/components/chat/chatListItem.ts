import { Component, inject, input } from "@angular/core";
import AuthService from "~services/auth.service";
import { Details } from "~services/chat.service";

@Component({
    selector: "chat-list-item",
    imports: [],
    templateUrl: "./chatListItem.html"
})
export class ChatListItem {

    authService = inject(AuthService);

    chatPreview = input.required<Details>();

}