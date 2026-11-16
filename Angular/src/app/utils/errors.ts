import { HttpErrorResponse } from "@angular/common/http";
import { translations } from "../translations";
import { catchError, Observable } from "rxjs";

export function translateError(error: HttpErrorResponse) {
    const status = error.status;
    if (status == 502 || status == 0) throw new Error(translations.serverConnectionFail);
    if (status / 100 == 5) throw new Error(translations.internalServerError);
}

export function handleHttpError<T, O>(handle?: (error: HttpErrorResponse, caugth: Observable<T>) => O) {
    return catchError((error: HttpErrorResponse, caught: Observable<T>) => {
        console.error(new Error(error.message))
        translateError(error);
        if (handle) handle(error, caught);
        throw new Error(translations.unknownError);
    });
}
