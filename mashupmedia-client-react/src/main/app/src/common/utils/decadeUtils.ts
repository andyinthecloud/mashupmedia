export function getDecades(): number[] {
    const decades: number[] = []
    for (let decade = 1920; decade < new Date().getFullYear(); decade += 10) {
        decades.push(decade)
    }
    return decades
}