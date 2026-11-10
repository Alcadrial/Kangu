import { inject, Injectable } from "@angular/core";
import { EMPTY, map, Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { handleHttpError } from "~utils/errors";

type RawMessage = {
    sender: string;
    receiver: string;
    message: string;
    messageMimeType: string;
    sendTimestamp: string;
    partIndex: number;
}

type MessagesResponse = {
    messages: RawMessage[],
    details: Details
}

type ChatsResponse = ChatPreview[];
type SendResponse = RawMessage[];

export type MessagePart = {
    message: string;
    mimeType: string;
}

export type Message = {
    sender: string;
    receiver: string;
    parts: MessagePart[];
    timestamp: string;
}

export type ChatPreview = {
    username: string;
    lastMessage: string;
    lastMessageMimeType: string;
    messageTimestamp: string;
    receiveTimestampOther: string;
    readTimestampSelf: string;
    readTimestampOther: string;
    isBlockedBySelf: boolean;
    isBlockedByOther: boolean;
}

export type Details = {
    receiveTimestampOther: string;
    readTimestampSelf: string;
    readTimestampOther: string;
    isBlockedBySelf: boolean;
    isBlockedByOther: boolean;
}

export type ChatDetailsAndMessages = {
    messages: Message[],
    details: Details
}

export type ChatPreviewAndMessages = {
    messages: Message[],
    preview: ChatPreview
}

@Injectable({ providedIn: "root" })
export default class ChatService {

    private readonly http = inject(HttpClient);

    data: ChatDetailsAndMessages | null = null;

    private collectMessagesParts(messages: RawMessage[]): Message[] {
        const newMessages: Message[] = [];
        let i = -1;
        for (const part of messages) {
            if (part.messageMimeType != "text/plain") continue;
            if (part.partIndex == 0) {
                newMessages[++i] = {
                    sender: part.sender,
                    receiver: part.receiver,
                    timestamp: part.sendTimestamp,
                    parts: []
                };
            }
            newMessages[i].parts.push({
                message: part.message,
                mimeType: part.messageMimeType
            });
        }
        return newMessages;
    }

    loadChats(): Observable<ChatPreview[]> {
        return this.http.get<ChatsResponse>("/api/messages/chats").pipe(handleHttpError());
    }

    loadChatMessages(user: string): Observable<ChatDetailsAndMessages> {
        return this.http.get<MessagesResponse>("/api/messages/get", {
            params: {
                user: user
            }
        }).pipe(
            map(r => this.data = {
                messages: this.collectMessagesParts(r.messages).reverse(),
                details: r.details
            }),
            handleHttpError()
        );
    }

    sendMessage(receiver: string, message: string): Observable<Message[]> {
        return this.http.post<SendResponse>("/api/messages/send", { receiver, message }).pipe(
            map(r => {
                if (!this.data) return [];
                const message = this.collectMessagesParts(r)[0];
                this.data.messages.push(message);
                return this.data.messages;
            }),
            handleHttpError()
        );
    }

    subscribeMessages(receiver: string): Observable<Message> {
        //implement websocket connection
        return EMPTY;
    }

}