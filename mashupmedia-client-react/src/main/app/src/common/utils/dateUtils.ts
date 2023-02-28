export const displayDateTime = (dateValue?: string | null): string => {
    if (dateValue === undefined || dateValue === null) {
        return ""
    }

    const date = new Date(dateValue)
    return date.toLocaleString() 
}

export const displayDuration = (seconds: number): string => {
    const roundedSeconds = Math.round(seconds)
    const displaySeconds = String(roundedSeconds % 60).padStart(2, '0')
    const displayMinutes = Math.round(roundedSeconds/60) 
    return `${displayMinutes}:${displaySeconds}`
}


