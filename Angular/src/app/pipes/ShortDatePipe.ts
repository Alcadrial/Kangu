import { Pipe, PipeTransform } from "@angular/core";


@Pipe({
    name: "sameDay"
})
export class SameDayPipe implements PipeTransform {
    transform(value: string): boolean {

        if (!/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z/.test(value)) throw new Error(`Invalid date: ${value}`);
        const date = new Date(value);
        if (isNaN(date.getTime())) throw new Error(`Invalid date: ${value}`);

        const now = new Date();

        return date.getFullYear() == now.getFullYear() &&
            date.getMonth() == now.getMonth() &&
            date.getDate() == now.getDate();
    }
}