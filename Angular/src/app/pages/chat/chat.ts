import { Component, computed, effect, inject, input, signal } from "@angular/core";
import { ChatList } from "../../components/chat/chatList";
import { ChatMessages } from "../../components/chat/chatMessages";
import { ActivatedRoute } from "@angular/router";
import AlertService from "~services/alert.service";
import ChatService, { ChatDetailsAndMessages, Details } from "~services/chat.service";
import UserService from "~services/user.service";

@Component({
    selector: "chat-root",
    imports: [ChatList, ChatMessages],
	templateUrl: "./chat.html"
})
export class Chat {
    private readonly route = inject(ActivatedRoute);
    private readonly alertService = inject(AlertService);
    private readonly chatService = inject(ChatService);
    private readonly userService = inject(UserService);

    readonly user = input<string>();

    readonly sender = computed(() => this.userService.user()?.username);
    readonly receiver = computed(() => this.user());
/*
    private readonly currentPath = toSignal(this.router.events.pipe(
        filter((event) => event instanceof NavigationEnd),
        map((event: NavigationEnd) => new URL(event.urlAfterRedirects))
    ));*/

    readonly currentChat = signal<ChatDetailsAndMessages | null>(null);
    readonly isLoadingMessages = computed<boolean>(() => !!this.currentChat());

    readonly chats = signal<Details[] | null>(null);
    readonly isLoadingChats = computed<boolean>(() => !!this.chats());

    readonly messageDraft = signal<string>("");
    readonly isSendingMessage = signal<boolean>(false);

    constructor() {
        effect(() => {
            if (this.sender()) {
                this.resetDraft();
                this.loadChats();
                this.loadChatMessages();
            }
        });
    }

    resetDraft() {
        this.messageDraft.set("")
    }

    loadChats() {
        this.chats.set(null);
        this.chatService.loadChats().subscribe({
            next: details => {
                this.chats.set(details);
            },
            error: e => {
                this.alertService.notifyError(e as string);
            }
        });
    }

    loadChatMessages() {
        const receiver = this.receiver();
        if (!receiver) return;
        this.currentChat.set(null);
        this.chatService.loadChatMessages(receiver).subscribe({
            next: chat => {
                this.currentChat.set(chat);
            },
            error: e => {
                this.alertService.notifyError(e as string);
            }
        })
    }
    
    sendMessage(message: string): boolean {
        const userId = this.receiver();
        if (!userId) return false;
        const oldChat = this.currentChat();
        if (!oldChat) return false;

        this.chatService.sendMessage(userId, message).subscribe({
            next: newMessage => {
                this.currentChat.set({
                    messages: [...oldChat.messages, newMessage],
                    details: oldChat.details
                });
            },
            error: e => {
                this.alertService.notifyError(e as string);
            }
        });
        return true;
    }
}
