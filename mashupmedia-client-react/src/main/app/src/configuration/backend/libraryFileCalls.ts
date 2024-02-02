import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"

const libraryFileUri = '/api/private/library/file/'

export type LibraryFilePayload = {
    name: string
    folder: boolean
    path: string
}

export const getLibraryFiles = (libraryId: number, folderPath?: string, userToken?: string): Promise<HttpResponse<LibraryFilePayload[]>> => {
    const uriParameters = folderPath ? `?folderPath=${encodeURI(folderPath)}` : ''
    return callMashupMediaApi<LibraryFilePayload[]> (HttpMethod.GET, `${libraryFileUri}${libraryId}${uriParameters}`, userToken)
}