export type MetaPayload = {
    id: number
    rank: number
}

export enum ArrayMoveType {
    TOP, UP, BOTTOM, DOWN, DELETE
}

export function updateMetaPayloadInArray(metaPayloads: MetaPayload[], metaPayload: MetaPayload, arrayMoveType: ArrayMoveType): void {

    if (!metaPayloads?.length) {
        return
    }

    const foundIndex = metaPayloads.findIndex(o => o.id === metaPayload.id)
    let index = 0

    if (foundIndex < 0) {
        return
    }

    switch (arrayMoveType) {
        case ArrayMoveType.TOP:
            index = 0
            break
        case ArrayMoveType.UP:
            index = foundIndex - 1
            break
        case ArrayMoveType.BOTTOM:
            index = metaPayloads.length - 1
            break
        case ArrayMoveType.DOWN:
            index = foundIndex + 1
            break
        default:
            index = 0
            break;
    }

    metaPayloads.splice(foundIndex, 1)
  
    if (arrayMoveType !== ArrayMoveType.DELETE) {
        metaPayloads.splice(index, 0, metaPayload)
    }
} 