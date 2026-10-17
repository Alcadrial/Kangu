
export function count(string: string, char: string, maxCount?: number): number {
    if (char.length != 1) throw new Error("Parameter \"char\" must be a string with only 1 character");
    let sum = 0;
    if (maxCount === undefined) {
        for (const c of string) if (c == char) sum++;
    }
    else {
        maxCount = Math.min(maxCount, string.length);
        for (let i = 0; i < maxCount; i++) if (string[i] == char) sum++;
    }
    return sum;
}

export function countAny(string: string, chars: string, maxCount?: number): number {
    if (chars.length == 0) return 0;
    let sum = 0;
    if (maxCount === undefined) {
        for (const c of string) if (chars.indexOf(c) != -1) sum++;
    }
    else {
        maxCount = Math.min(maxCount, string.length);
        for (let i = 0; i < maxCount; i++) if (chars.indexOf(string[i]) != -1) sum++;
    }
    return sum;
}

export function countRegex(string: string, regex: RegExp, maxCount?: number): number {
    let sum = 0;
    if (maxCount === undefined) {
        for (const c of string) if (regex.test(c)) sum++;
    }
    else {
        maxCount = Math.min(maxCount, string.length);
        for (let i = 0; i < maxCount; i++) if (regex.test(string[i])) sum++;
    }
    return sum;
}

export function countCustom(string: string, checkChar: (c: string) => boolean, maxCount?: number): number {
    let sum = 0;
    if (maxCount === undefined) {
        for (const c of string) if (checkChar(c)) sum++;
    }
    else {
        maxCount = Math.min(maxCount, string.length);
        for (let i = 0; i < maxCount; i++) if (checkChar(string[i])) sum++;
    }
    return sum;
}