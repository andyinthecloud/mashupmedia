import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"
import { MashupMediaType } from "../../media/music/rest/playlistActionCalls"

const libraryFileUri = '/api/private/library/file/'

export type BreadcrumbPayload = {
    name: string
    path: string
}

export type LibraryFilePayload = {
    name: string
    folder: boolean
    path: string
    mashupMediaType: MashupMediaType
}

export type LibraryFilesPayload = {
    breadcrumbPayloads: BreadcrumbPayload[]
    libraryFilePayloads: LibraryFilePayload[]
}

export type LibraryRenameFilePayload = {
    libraryId: number
    path: string;    
    name: string;
}

export type LibraryDeleteFilePayload =  {
    libraryId: number;
    path: string    
}

export const getLibraryFiles = (libraryId: number, folderPath?: string, userToken?: string): Promise<HttpResponse<LibraryFilesPayload>> => {
    const uriParameters = folderPath ? `?folderPath=${encodeURI(folderPath)}` : ''
    return callMashupMediaApi<LibraryFilesPayload> (HttpMethod.GET, `${libraryFileUri}${libraryId}${uriParameters}`, userToken)
}

export const renameFile = (libraryRenameFilePayload: LibraryRenameFilePayload, userToken?: string): Promise<HttpResponse<boolean>> => {
    console.log("renameFile", libraryRenameFilePayload)

    return callMashupMediaApi<boolean> (HttpMethod.PUT, libraryFileUri + 'rename', userToken, JSON.stringify(libraryRenameFilePayload))
}

export const deleteFile = (libraryDeleteFilePayload: LibraryDeleteFilePayload, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean> (HttpMethod.PUT, libraryFileUri + 'delete', userToken, JSON.stringify(libraryDeleteFilePayload))
}
