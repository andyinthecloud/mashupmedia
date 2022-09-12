import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils";
import { NameValuePayload } from "./metaCalls";

export enum LibraryTypePayload {
    MUSIC, VIDEO, PHOTO
}

export type LibraryNameValuePayload = NameValuePayload<number> & LibraryTypePayload

const metaUri = '/api/library/'

export const getLibraries = (userToken?: string): Promise<HttpResponse<LibraryNameValuePayload[]>> => {
    return callMashupMediaApi<LibraryNameValuePayload[]> (HttpMethod.GET, metaUri + 'all', userToken)
}
