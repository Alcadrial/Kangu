import { Pipe, PipeTransform } from "@angular/core";


@Pipe({
    name: "pastTime"
})
export class PastTimePipe implements PipeTransform {
    transform(value: string): string {
        
        if (!/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z/.test(value)) return "unknown time";
        const date = new Date(value);
        if (isNaN(date.getTime())) return "unknown time";

        const diff = new Date(new Date().getTime() - date.getTime());
        const diffSeconds = diff.getTime() / 1000 | 0;

        if (Math.abs(diffSeconds) < 10) return "just now";

        const isFuture = diffSeconds > 0;

        if (Math.abs(diffSeconds) < 60) return `${diffSeconds} second${diffSeconds == 1 ? "" : "s"} ago`;

        const diffMinutes = diffSeconds / 60 | 0;
        if (Math.abs(diffMinutes) < 60) return `${diffMinutes} minute${diffMinutes == 1 ? "" : "s"} ago`;

        const diffHours = diffMinutes / 60 | 0;
        if (Math.abs(diffHours) < 24) return `${diffHours} hour${diffHours == 1 ? "" : "s"} ago`;

        const diffDays = diffHours / 24 | 0;
        if (Math.abs(diffDays) < 14) return `${diffDays} day${diffDays == 1 ? "" : "s"} ago`;

        const diffWeeks = diffDays / 7 | 0;
        if (Math.abs(diffWeeks) < 8) return `${diffWeeks} week${diffWeeks == 1 ? "" : "s"} ago`;

        const diffMonths = diffDays / 30 | 0;
        if (Math.abs(diffMonths) < 12) return `${diffMonths} month${diffMonths == 1 ? "" : "s"} ago`;

        const diffYears = diff.getFullYear();
        return `${diffYears} year${diffYears == 1 ? "" : "s"} ago`;
    }
}