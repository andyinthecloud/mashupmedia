export type DialogPageload<P> = {
    open: boolean
    title: string
    payload: P
    // updatePayload(payload: P): void
}

export type DialogWithUpdateCallPageload<P> = DialogPageload<P> & {
    updatePayload(payload: P): void
}