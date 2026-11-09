import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { catchError, of } from "rxjs";
import { handleHttpError } from "~utils/errors";
import { OnlineStatus } from "~utils/types";

export type UserData = {
    username: string;
    email: string;
    isAdmin: boolean;
	registrationTime: Date;
	lastAccessTime: Date;
	lastActionTime: Date;
	onlineStatus: OnlineStatus;
}

@Injectable({ providedIn: "root" })
export default class UserService {

    private readonly http = inject(HttpClient);

    private readonly _error = signal<string | null>(null);
    readonly error = this._error.asReadonly();
    readonly user = toSignal(this.http.get<UserData>("/api/user").pipe(
        handleHttpError(),
        catchError((error: Error) => {
            this._error.set(error.message);
            return of(null);
        })
    ));
    
}