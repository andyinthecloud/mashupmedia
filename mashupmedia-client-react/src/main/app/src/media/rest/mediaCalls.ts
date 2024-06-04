export type MetaEntityPayload = {
    id: number
    rank: number
}

export type ExternalLinkPayload = MetaEntityPayload & {
    name: string
    link: string
}

export type MenuMetaPayload<P> = {
    open: boolean
    payload?: P
    anchorElement: HTMLElement | null
    edit: (payload: P) => void
    delete: (payload: P) => void
    moveTop: (payload: P) => void
    moveUpOne: (payload: P) => void
    moveDownOne: (payload: P) => void
    moveBottom: (payload: P) => void    
}