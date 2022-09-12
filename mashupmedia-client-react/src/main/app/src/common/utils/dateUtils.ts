export const displayDateTime = (dateValue?: string | null): string => {
    if (dateValue === undefined || dateValue === null) {
        return ""
    }

    const date = new Date(dateValue)
    return date.toLocaleString() 
}
