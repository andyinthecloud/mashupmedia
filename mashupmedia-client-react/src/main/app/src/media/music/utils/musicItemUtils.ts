export const getTrackYearInBrackets = (year?: number): string => {
    if (!year) {
        return ""
    }
    return `(${year})`
}