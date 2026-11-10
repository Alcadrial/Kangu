import { TestBed } from "@angular/core/testing";
import ChatService, { ChatDetailsAndMessages, ChatPreview, Details } from "./chat.service";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { firstValueFrom } from "rxjs";
import { HTTP_ERRORS } from "~utils/test";
import { translations } from "../translations";

describe("chat service", () => {
    let service: ChatService;
    let http: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [ChatService, provideHttpClientTesting()]
        });
        http = TestBed.inject(HttpTestingController)
        service = TestBed.inject(ChatService);
    });

    afterEach(() => {
        http.verify();
    });

    describe("data", () => {
        it("initial state", async () => {
            assert.notExists(service.data);
        });
    });

    describe("loadChats", () => {
        it("success", async () => {
            const value: ChatPreview[] = [{
                username: "user",
                lastMessage: "message",
                lastMessageMimeType: "text/plain",
                messageTimestamp: new Date().toISOString(),
                receiveTimestampOther: new Date().toISOString(),
                readTimestampSelf: new Date().toISOString(),
                readTimestampOther: new Date().toISOString(),
                isBlockedBySelf: false,
                isBlockedByOther: false
            }]
            const promise = firstValueFrom(service.loadChats());

            http.expectOne({
                url: "/api/messages/chats",
                method: "GET"
            }).flush(value);

            assert.deepEqual(await promise, value);
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.loadChats());
            http.expectOne("/api/messages/chats").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.loadChats());
            http.expectOne("/api/messages/chats").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });


    const T_0 = new Date(2026, 1, 1, 0, 0).toISOString();
    const T_1 = new Date(2026, 1, 1, 1, 0).toISOString();
    const T_2 = new Date(2026, 1, 1, 2, 0).toISOString();
    const T_3 = new Date(2026, 1, 1, 3, 0).toISOString();
    const details = {
        username: "user",
        lastMessage: "message",
        lastMessageMimeType: "text/plain",
        messageTimestamp: T_0,
        receiveTimestampOther: T_1,
        readTimestampSelf: T_2,
        readTimestampOther: T_3,
        isBlockedBySelf: false,
        isBlockedByOther: false
    };
    const parts = [
        {
            sender: "user",
            receiver: "user1",
            message: "message",
            messageMimeType: "text/plain",
            sendTimestamp: T_0,
            partIndex: 0,
        }, {
            sender: "user",
            receiver: "user1",
            message: "message1",
            messageMimeType: "text/plain",
            sendTimestamp: T_0,
            partIndex: 1,
        }, {
            sender: "user",
            receiver: "user1",
            message: "message2",
            messageMimeType: "text/plain",
            sendTimestamp: T_1,
            partIndex: 0,
        }
    ];
    const merged = [
        {
            sender: "user",
            receiver: "user1",
            parts: [
                { message: "message", mimeType: "text/plain" },
                { message: "message1", mimeType: "text/plain" }
            ],
            timestamp: T_0,
        }, {
            sender: "user",
            receiver: "user1",
            parts: [
                { message: "message2", mimeType: "text/plain" }
            ],
            timestamp: T_1,
        },
    ];

    describe("loadChatMessages", () => {
        it("success", async () => {
            const promise = firstValueFrom(service.loadChatMessages("user1"));

            http.expectOne({
                url: "/api/messages/get?user=user1",
                method: "GET"
            }).flush({ messages: parts, details });

            assert.deepEqual(await promise, { messages: merged, details });
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.loadChatMessages("user1"));
            http.expectOne("/api/messages/get?user=user1").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.loadChatMessages("user1"));
            http.expectOne("/api/messages/get?user=user1").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe("sendMessage", () => {
        it("success", async () => {
            const promise = firstValueFrom(service.sendMessage("user1", "message2"));

            http.expectOne(r =>
                r.url == "/api/messages/send" &&
                r.method == "POST" &&
                r.body.receiver == "user1" &&
                r.body.message == "message2"
            ).flush({ messages: parts.slice(2, 3), details });

            assert.deepEqual(await promise, merged.slice(1, 1));
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.sendMessage("user1", "message2"));
            http.expectOne("/api/messages/send").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.sendMessage("user1", "message2"));
            http.expectOne("/api/messages/send").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe.todo("subscribeMessages", () => {
        it.todo("success", async () => {
            const promise = firstValueFrom(service.subscribeMessages("user1"));

            http.expectOne(r =>
                r.url == "/api/messages/send" &&
                r.method == "POST" &&
                r.body.receiver == "user1" &&
                r.body.message == "message2"
            ).flush({ messages: parts.slice(2, 3), details });

            assert.deepEqual(await promise, merged[1]);
        });
        it.todo.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.subscribeMessages("user1"));
            http.expectOne("/api/messages/send").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it.todo("fail with network error", async () => {
            const promise = firstValueFrom(service.subscribeMessages("user1"));
            http.expectOne("/api/messages/send").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });
});
