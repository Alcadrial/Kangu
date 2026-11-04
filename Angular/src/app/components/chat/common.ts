import { WritableSignal } from "@angular/core";

export function setValue<T>(signal: WritableSignal<T>, e: Event, parser: (value: string) => T = v => v as T) {
    signal.set(parser((e.target as HTMLInputElement).value));
}