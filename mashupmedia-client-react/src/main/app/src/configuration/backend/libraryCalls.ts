import { ServerResponsePayload } from '../../common/utils/form-validation-utils';
import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils";
import { NameValuePayload } from './metaCalls';

export enum LibraryTypePayload {
    MUSIC = 'MUSIC', 
    VIDEO = 'VIDEO', 
    PHOTO = 'PHOTO'
}

export type LibraryNameValuePayload = NameValuePayload<number> & LibraryTypePayload

export type LibraryPayload = {
    id?: number
    name: string
    path: string
    createdOn?: string
    createdBy?: string
    updatedOn?: string
    updatedBy?: string
    enabled: boolean
    lastSuccessfulScanOn?: string
    groups: NameValuePayload<number>[]
    libraryTypePayload: LibraryTypePayload
    albumArtImagePattern?: string    
}

const libraryUri = '/api/library/'

export const getLibraries = (userToken?: string): Promise<HttpResponse<LibraryNameValuePayload[]>> => {
    return callMashupMediaApi<LibraryNameValuePayload[]> (HttpMethod.GET, libraryUri + 'all', userToken)
}

export const getLibrary = (libraryId: number, userToken?: string): Promise<HttpResponse<LibraryPayload>> => {
    return callMashupMediaApi<LibraryPayload> (HttpMethod.GET, libraryUri + libraryId, userToken)
}

export const checkLibraryPathExists = (path: string, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    const valuePayload: NameValuePayload<string> = {
        name: 'path',
        value: path
    }

    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.POST, libraryUri + 'check-path', userToken, JSON.stringify(valuePayload))
}

export const saveLibrary = (libraryPayload: LibraryPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.PUT, libraryUri, userToken, JSON.stringify(libraryPayload))
}

export const deleteLibrary = (libraryId: number, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean> (HttpMethod.DELETE, libraryUri + libraryId, userToken)
}