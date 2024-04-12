export type DialogPageload<P> = {
    open: boolean
    title: string
    payload: P
    updatePayload(payload: P): void
}