export type PagePayload<T> = {
    totalPages: number
    totalElements: number
    size: number
    pageNumber: number
    isFirst: boolean
    isLast: boolean
    hasNext: boolean
    hasPrevious: boolean
    content: T[]
}
