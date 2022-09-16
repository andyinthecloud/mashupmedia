import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils";
import { NameValuePayload } from "./metaCalls";

export enum LibraryTypePayload {
    MUSIC = 'music', 
    VIDEO = 'video', 
    PHOTO = 'photo'
}

export type LibraryNameValuePayload = NameValuePayload<number> & LibraryTypePayload

export type LibraryPayload = {
    id?: number
    name: string
    path: string
    createdOn?: Date
    createdBy?: string
    updatedOn?: Date
    updatedBy?: string
    enabled: boolean
    lastSuccessfulScanOn?: Date
    groups: NameValuePayload<number>[]
    libraryTypePayload: LibraryTypePayload
    albumArtImagePattern?: string    
}

const metaUri = '/api/library/'

export const getLibraries = (userToken?: string): Promise<HttpResponse<LibraryNameValuePayload[]>> => {
    return callMashupMediaApi<LibraryNameValuePayload[]> (HttpMethod.GET, metaUri + 'all', userToken)
}

export const getLibrary = (libraryId: number, userToken?: string): Promise<HttpResponse<LibraryPayload>> => {
    return callMashupMediaApi<LibraryPayload> (HttpMethod.GET, metaUri + libraryId, userToken)
}