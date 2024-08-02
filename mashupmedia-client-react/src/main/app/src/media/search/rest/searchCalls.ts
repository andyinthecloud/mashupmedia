import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../../common/utils/httpUtils"
import { NameValuePayload } from "../../../configuration/backend/metaCalls"

const searchUri = "/api/private/search"
const searchMediaUri = searchUri + "/media"

export const getOrderByNames = (userToken?: string): Promise<HttpResponse<NameValuePayload<string>[]>> => {
    return callMashupMediaApi<NameValuePayload<string>[]>(HttpMethod.GET, `${searchMediaUri}/orderByNames`, userToken)
}