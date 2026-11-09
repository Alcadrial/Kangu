import { HttpErrorResponse } from "@angular/common/http";
import { translations } from "../translations";
import { catchError, Observable } from "rxjs";

export function translateError(error: HttpErrorResponse) {
    const status = error.status;
    if (status == 502) throw translations.serverConnectionFail;
    if (status / 100 == 5) throw translations.internalServerError;
}

export function handleHttpError<T, O>(handle?: (error: HttpErrorResponse, caugth: Observable<T>) => O) {
    return catchError((error: HttpErrorResponse, caught: Observable<T>) => {
        translateError(error);
        if (handle) handle(error, caught);
        throw error.error.message;
    });
}
