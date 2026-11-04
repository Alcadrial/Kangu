import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";

export type UserData = {
    username: string;
    email: string;
    isAdmin: boolean;
}

@Injectable({ providedIn: "root" })
export default class UserService {

    private readonly http = inject(HttpClient);

    readonly user = signal<UserData>(undefined as unknown as UserData);

    
}