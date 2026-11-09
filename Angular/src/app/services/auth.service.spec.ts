import { TestBed } from "@angular/core/testing";
import AuthService from "./auth.service";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { firstValueFrom } from "rxjs";
import { authData, HTTP_ERRORS } from "~utils/test";
import { translations } from "../translations";

describe("auth service", () => {
    let service: AuthService;
    let http: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [AuthService, provideHttpClientTesting()]
        });
        http = TestBed.inject(HttpTestingController)
        service = TestBed.inject(AuthService);
    });

    afterEach(() => {
        http.verify();
    });

    describe("getCsrfToken", () => {
        it("success", async () => {
            const token = { csrf: "csrf" };

            let promise = firstValueFrom(service.getCsrfToken());

            http.expectOne({
                url: "/api/csrf",
                method: "GET"
            }).flush(token);

            assert.strictEqual(await promise, token.csrf);
        });
        it("cache token", async () => {
            const token = { csrf: "csrf" };

            let promise = firstValueFrom(service.getCsrfToken());

            http.expectOne({
                url: "/api/csrf",
                method: "GET"
            }).flush(token);

            assert.strictEqual(await promise, token.csrf);

            promise = firstValueFrom(service.getCsrfToken());

            assert.strictEqual(await promise, token.csrf);
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.getCsrfToken());
            http.expectOne("/api/csrf").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.getCsrfToken());
            http.expectOne("/api/csrf").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe("login", () => {
        it("success", async () => {
            const data = {
                username: "user",
                password: "password",
                rememberMe: false
            };

            const promise = firstValueFrom(service.login(data.username, data.password, data.rememberMe));

            http.expectOne(r =>
                r.url == "/api/login" &&
                r.method == "POST" &&
                r.body.username == data.username &&
                r.body.password == data.password &&
                Boolean(r.body.rememberMe) == data.rememberMe
            ).flush(authData);

            assert.strictEqual(await promise, authData);
        });
        it.todo("success rememberMe", async () => {
            const data = {
                username: "user",
                password: "password",
                rememberMe: true
            };

            const promise = firstValueFrom(service.login(data.username, data.password, data.rememberMe));

            http.expectOne(r =>
                r.url == "/api/login" &&
                r.method == "POST" &&
                r.body.username == data.username &&
                r.body.password == data.password &&
                Boolean(r.body.rememberMe) == data.rememberMe
            ).flush(authData);

            assert.strictEqual(await promise, authData);
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.login("user", "password", false));
            http.expectOne("/api/login").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.login("user", "password", false));
            http.expectOne("/api/login").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe("register", () => {
        it("success", async () => {
            const data = {
                username: "user",
                password: "password",
                email: "g@g.g",
                rememberMe: false
            };

            const promise = firstValueFrom(service.register(data.username, data.email, data.password, data.rememberMe));

            http.expectOne(r =>
                r.url == "/api/register" &&
                r.method == "POST" &&
                r.body.username == data.username &&
                r.body.email == data.email &&
                r.body.password == data.password &&
                Boolean(r.body.rememberMe) == data.rememberMe
            ).flush(authData);

            assert.strictEqual(await promise, authData);
        });
        it.todo("success rememberMe", async () => {
            const data = {
                username: "user",
                password: "password",
                email: "g@g.g",
                rememberMe: true
            };

            const promise = firstValueFrom(service.register(data.username, data.email, data.password, data.rememberMe));

            http.expectOne(r =>
                r.url == "/api/register" &&
                r.method == "POST" &&
                r.body.username == data.username &&
                r.body.email == data.email &&
                r.body.password == data.password &&
                Boolean(r.body.rememberMe) == data.rememberMe
            ).flush(authData);

            assert.strictEqual(await promise, authData);
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.register("user", "g@g.g", "password", false));
            http.expectOne("/api/register").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.register("user", "g@g.g", "password", false));
            http.expectOne("/api/register").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe("logout", () => {
        it("success", async () => {
            const promise = firstValueFrom(service.logout());

            http.expectOne({
                url: "/api/logout",
                method: "POST"
            }).flush("");

            await expect(promise).resolves.not.toThrow();
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.logout());
            http.expectOne("/api/logout").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.logout());
            http.expectOne("/api/logout").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });

    describe("hasRole", () => {
        it("success with role", async () => {
            const role = "USER";
            const value = true;

            const promise = firstValueFrom(service.hasRole(role));

            http.expectOne({
                url: "/api/auth",
                method: "GET"
            }).flush(authData);

            await expect(promise).resolves.equals(value);
        });
        it("success without role", async () => {
            const role = "ADMIN";
            const value = false;

            const promise = firstValueFrom(service.hasRole(role));

            http.expectOne({
                url: "/api/auth",
                method: "GET"
            }).flush(authData);

            await expect(promise).resolves.equals(value);
        });
        it("success login cache with sync flow", async () => {
            const role = "ADMIN";
            const value = false;

            const loginPromise = firstValueFrom(service.login("user", "password", false));
            http.expectOne("/api/login").flush(authData);
            await loginPromise;

            const promise = firstValueFrom(service.hasRole(role));
            http.expectNone("/api/auth");
            await expect(promise).resolves.equals(value);
        });
        it("success register cache with sync flow", async () => {
            const role = "ADMIN";
            const value = false;

            const loginPromise = firstValueFrom(service.register("user", "g@g.g", "password", false));
            http.expectOne("/api/register").flush(authData);
            await loginPromise;

            const promise = firstValueFrom(service.hasRole(role));

            http.expectNone("/api/auth");
            await expect(promise).resolves.equals(value);
        });
        it("clear cache after logout", async () => {
            const role = "ADMIN";
            const value = false;

            const loginPromise = firstValueFrom(service.login("user", "password", false));
            http.expectOne("/api/login").flush(authData);
            await loginPromise;

            const logoutPromise = firstValueFrom(service.logout());
            http.expectOne("/api/logout").flush("");
            await logoutPromise;

            const promise = firstValueFrom(service.hasRole(role));
            http.expectOne("/api/auth").flush(authData);
            await expect(promise).resolves.equals(value);
        });
        it.for(HTTP_ERRORS)("fail with status $status", async ({ status, error }) => {
            const promise = firstValueFrom(service.hasRole("USER"));
            http.expectOne("/api/auth").flush("fail", { status, statusText: error });
            await expect(promise).rejects.toThrow(error);
        });
        it("fail with network error", async () => {
            const promise = firstValueFrom(service.hasRole("USER"));
            http.expectOne("/api/auth").error(new ProgressEvent("Error"));
            await expect(promise).rejects.toThrow(translations.serverConnectionFail);
        });
    });
});
