import { TestBed } from "@angular/core/testing";
import UserService from "./user.service";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { HTTP_ERRORS, userData } from "~utils/test";
import { firstValueFrom } from "rxjs";
import { translations } from "../translations";

describe("user service", () => {
    let service: UserService;
    let http: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [UserService, provideHttpClientTesting()]
        });
        http = TestBed.inject(HttpTestingController)
        service = TestBed.inject(UserService);
    });

    afterEach(() => {
        http.verify();
    });

    describe("user", () => {
        it("initial state", async () => {
            assert.notExists(service.user());
            assert.notExists(service.error());

            http.expectOne({
                url: "/api/user",
                method: "GET"
            });
        });
        it("success", async () => {
            http.expectOne({
                url: "/api/user",
                method: "GET"
            }).flush(userData);

            assert.strictEqual(service.user(), userData);
            assert.notExists(service.error());
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            http.expectOne("/api/user").flush("fail", { status, statusText: error });
            assert.notExists(service.user());
            assert.strictEqual(service.error(), error);
        });
        it("fail with network error", async () => {
            http.expectOne("/api/user").error(new ProgressEvent("Error"));
            assert.notExists(service.user());
            assert.strictEqual(service.error(), translations.serverConnectionFail);
        });
    });
});
