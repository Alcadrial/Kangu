import { Component, computed, effect, inject, input, signal } from "@angular/core";
import { ChatList } from "../../components/chat/chatList";
import { ChatMessages } from "../../components/chat/chatMessages";
import AlertService from "~services/alert.service";
import ChatService, { ChatDetailsAndMessages, ChatPreview, ChatPreviewAndMessages } from "~services/chat.service";
import UserService from "~services/user.service";

@Component({
    selector: "chat-root",
    imports: [ChatList, ChatMessages],
	templateUrl: "./chat.html"
})
export class Chat {
    private readonly alertService = inject(AlertService);
    private readonly chatService = inject(ChatService);
    private readonly userService = inject(UserService);

    readonly user = input<string>();
    
    readonly sender = computed(() => this.userService.user()?.username);
    readonly receiver = computed(() => this.user());
    
    readonly currentChat = signal<ChatPreviewAndMessages | null>(null);
    readonly isLoadingMessages = computed<boolean>(() => !this.currentChat());
    
    readonly chats = signal<ChatPreview[] | null>(null);
    readonly isLoadingChats = computed<boolean>(() => !this.chats());
    
    readonly messageDraft = signal<string>("");
    readonly isSendingMessage = signal<boolean>(false);

    readonly error = signal<string | null>(null);

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
            error: (e: Error) => {
                this.alertService.notifyError(e.message);
            }
        });
    }

    loadChatMessages() {
        const receiver = this.receiver();
        if (!receiver) return;

        this.currentChat.set(null);
        this.chatService.loadChatMessages(receiver).subscribe({
            next: chat => {
                const lastMessage = chat.messages[0];
                this.currentChat.set({
                    messages: chat.messages,
                    preview: {
                        ...chat.details,
                        username: receiver,
                        lastMessage: lastMessage.parts[0].message,
                        lastMessageMimeType: lastMessage.parts[0].mimeType,
                        messageTimestamp: lastMessage.timestamp
                    }
                });
            },
            error: (e: Error) => {
                this.alertService.notifyError(e.message);
                this.error.set("An error occurred while loading the chats");
            }
        });
    }
    
    sendMessage(message: string): boolean {
        const userId = this.receiver();
        if (!userId) return false;
        const oldChat = this.currentChat();
        if (!oldChat) return false;

        this.chatService.sendMessage(userId, message).subscribe({
            next: messages => {
                this.currentChat.set({
                    messages,
                    preview: oldChat.preview
                });
                this.resetDraft();
            },
            error: (e: Error) => {
                this.alertService.notifyError(e.message);
                this.error.set("An error occurred while loading the chats");
            }
        });
        return true;
    }
}
