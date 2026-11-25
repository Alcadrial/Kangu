import { Component, computed, DestroyRef, effect, inject, input, signal, untracked } from "@angular/core";
import { ChatList } from "../../components/chat/chatList";
import { ChatMessages } from "../../components/chat/chatMessages";
import AlertService from "~services/alert.service";
import ChatService, { ChatPreview, ChatPreviewAndMessages } from "~services/chat.service";
import UserService from "~services/user.service";
import AuthService from "~services/auth.service";
import { Router } from "@angular/router";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Subscription } from "rxjs";

@Component({
    selector: "chat-root",
    imports: [ChatList, ChatMessages],
    templateUrl: "./chat.html"
})
export class Chat {
    private readonly alertService = inject(AlertService);
    private readonly chatService = inject(ChatService);
    private readonly userService = inject(UserService);
	private readonly authService = inject(AuthService);
	private readonly router = inject(Router);
    private readonly destroyRef = inject(DestroyRef);

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

    private lastSubscriptions?: Subscription;

    constructor() {
        effect(() => {
            if (this.sender()) {
                this.resetDraft();
                this.loadChats();
                this.loadChatMessages();
            }
        });
        effect(() => {
            if (this.sender()) {
                this.subscribe();
            }
        });
    }

    resetDraft() {
        this.messageDraft.set("")
    }

    loadChats() {
        this.chats.set(null);
        this.chatService.loadChats()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
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
        this.chatService.loadChatMessages(receiver)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: chat => {
                    const lastMessage = chat.messages[0];
                    this.currentChat.set({
                        messages: chat.messages,
                        preview: {
                            ...chat.details,
                            username: receiver,
							lastMessage: lastMessage?.parts?.[0]?.message ?? "",
							lastMessageMimeType: lastMessage?.parts?.[0]?.mimeType ?? "text/plain",
							messageTimestamp: lastMessage?.timestamp ?? new Date()
                        }
                    });
                },
                error: (e: Error) => {
                    this.alertService.notifyError(e.message);
                    this.error.set("An error occurred while loading the chats");
                }
            });
    }

    subscribe() {
        if (this.lastSubscriptions) this.lastSubscriptions.unsubscribe();

        const chat = this.currentChat();
        if (!chat) return;

        this.lastSubscriptions = this.chatService.subscribeMessages()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                complete: () => this.chatService.disconnect(),
                next: messages => {
                    this.currentChat.set({
                        messages,
                        preview: chat.preview
                    });
                },
                error: (e: Error) => {
                    this.alertService.notifyError(e.message);
                    this.error.set("An error occurred while delivering new messages");
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

    logout() {
        this.authService.logout()
            .subscribe({
                complete: () => this.router.navigate(["/"]),
                error: () => this.router.navigate(["/"])
            });
    }
}
