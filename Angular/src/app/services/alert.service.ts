import { Injectable } from "@angular/core";

@Injectable({ providedIn: "root" })
export default class AlertService {

    notifyError(message: string) {
        if (typeof window !== "undefined") {
            window.alert("An error occurred: " + message);
        }
    }
}