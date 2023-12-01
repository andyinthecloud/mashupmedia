import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../../common/utils/httpUtils"
import { NameValuePayload } from "../../../configuration/backend/metaCalls"

const searchUri = "/api/private/search"
const searchMediaUri = searchUri + "/media"

export type GenrePayload = {
    idName: string
    name: string
}

export const getGenres = (userToken?: string): Promise<HttpResponse<GenrePayload[]>> => {
    return callMashupMediaApi<GenrePayload[]>(HttpMethod.GET, `${searchMediaUri}/genres`, userToken)
}


export const getOrderByNames = (userToken?: string): Promise<HttpResponse<NameValuePayload<string>[]>> => {
    return callMashupMediaApi<NameValuePayload<string>[]>(HttpMethod.GET, `${searchMediaUri}/orderByNames`, userToken)
}