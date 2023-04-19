import { HttpMethod, HttpResponse, callMashupMediaApiNoRedirect } from "../../utils/httpUtils"

const loginUri = '/login/'

export const isLoggedIn = (): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApiNoRedirect<boolean> (HttpMethod.GET, loginUri + 'is-logged-in')
}