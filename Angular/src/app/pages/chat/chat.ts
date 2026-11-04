import { Component, computed, CUSTOM_ELEMENTS_SCHEMA, inject, OnInit, signal } from "@angular/core";
import { ChatList } from "../../components/chat/chatList";
import { ChatContent } from "../../components/chat/chat";
import { ActivatedRoute } from "@angular/router";
import { toSignal } from "@angular/core/rxjs-interop";
import { map, tap } from "rxjs";
import AlertService from "~services/AlertService";
import ChatService, { ChatDetailsAndMessages, Details } from "~services/ChatService";
import "@awesome.me/webawesome/dist/components/spinner/spinner.js";

@Component({
    selector: "chat-root",
    imports: [ChatList, ChatContent],
    templateUrl: "./chat.html",
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Chat implements OnInit {
    private readonly activatedRoute = inject(ActivatedRoute);
    private readonly alertService = inject(AlertService);
    private readonly chatService = inject(ChatService);

    readonly otherUser = toSignal(this.activatedRoute.paramMap.pipe(
        map(p => p.get("userId")),
        tap(user => this.otherUser && this.otherUser() != user && this.resetDraft())
    ), { initialValue: null });

    readonly chats = signal<Details[] | null>(null);
    readonly isLoadingChats = computed<boolean>(() => !!this.chats());
    readonly chat = signal<ChatDetailsAndMessages | null>(null);
    readonly isLoadingMessages = computed<boolean>(() => !!this.chat());
    readonly messageDraft = signal<string>("");
    readonly isSendingMessage = signal<boolean>(false);

    ngOnInit(): void {
        this.loadChats();
        this.loadChatMessages();
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
        const otherUser = this.otherUser();
        if (!otherUser) return;
        this.chat.set(null);
        this.chatService.loadChatMessages(otherUser).subscribe({
            next: chat => {
                this.chat.set(chat);
            },
            error: e => {
                this.alertService.notifyError(e as string);
            }
        })
    }

    sendMessage(message: string): boolean {
        const otherUser = this.otherUser();
        if (!otherUser) return false;
        const oldChat = this.chat();
        if (!oldChat) return false;

        this.chatService.sendMessage(otherUser, message).subscribe({
            next: newMessage => {
                this.chat.set({
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
