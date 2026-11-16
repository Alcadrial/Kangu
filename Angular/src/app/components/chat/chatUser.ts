import { Component, computed, inject } from "@angular/core";
import SpinnerComponent from "~components/spinner";
import { PastTimePipe } from "~pipes/PastTimePipe";
import UserService from "~services/user.service";

@Component({
    selector: "chat-user",
    imports: [SpinnerComponent, PastTimePipe],
    templateUrl: "./chatUser.html"
}) 
export class ChatUser {

    private readonly userService = inject(UserService);

    readonly userData = this.userService.user;
    readonly statusColor = computed(() => ({
        ON: "bg-status-online",
        AFK: "bg-status-afk",
        OFF: "bg-status-offline",
        "": "bg-status-unknown"
    }[this.userData()?.onlineStatus ?? ""]));
}