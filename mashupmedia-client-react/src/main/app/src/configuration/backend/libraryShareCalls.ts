import { ServerResponsePayload } from "../../common/utils/formValidationUtils"
import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"

const libraryShareUri = '/api/private/library/share/'

export type LibrarySharePayload = {
    libraryId: number
    email: string
}

export type LibraryShareUserPayload = {
    email: string
    name?: string
    validated: boolean
}


export const getLibraryShares = (libraryId: number, userToken?: string): Promise<HttpResponse<LibraryShareUserPayload[]>> => {
    return callMashupMediaApi<LibraryShareUserPayload[]> (HttpMethod.GET, libraryShareUri + libraryId, userToken)
}

export const addLibraryShare = (librarySharePayload: LibrarySharePayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<LibraryShareUserPayload[]>>> => {
    return callMashupMediaApi<ServerResponsePayload<LibraryShareUserPayload[]>> (HttpMethod.PUT, libraryShareUri, userToken, JSON.stringify(librarySharePayload))
}

export const deleteLibraryShare = (librarySharePayload: LibrarySharePayload, userToken?: string): Promise<HttpResponse<LibraryShareUserPayload[]>> => {
    return callMashupMediaApi<LibraryShareUserPayload[]> (HttpMethod.DELETE, libraryShareUri, userToken, JSON.stringify(librarySharePayload))
}
