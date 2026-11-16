import { Component, computed, effect, inject, input, signal } from "@angular/core";
import { PastTimePipe } from "~pipes/PastTimePipe";
import { ChatPreview } from "~services/chat.service";
import UserService, { UserData } from "~services/user.service";

@Component({
    selector: "chat-list-item",
    imports: [PastTimePipe],
    templateUrl: "./chatListItem.html"
}) 
export class ChatListItem {

    private readonly userService = inject(UserService);

    readonly chatPreview = input.required<ChatPreview>();
    readonly userData = signal<UserData | null>(null);
    readonly error = signal<string | null>(null);

    readonly statusColor = computed(() => ({
        ON: "bg-status-online",
        AFK: "bg-status-afk",
        OFF: "bg-status-offline",
        "": "bg-status-unknown"
    }[this.userData()?.onlineStatus ?? ""]));

    constructor() {
        effect(() => {
            this.userService.getUserData(this.chatPreview().username).subscribe({
                next: data => this.userData.set(data),
                error: (error: Error) => this.error.set(error.message)
            })
        });
    }
}