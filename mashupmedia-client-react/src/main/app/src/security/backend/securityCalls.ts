import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../utils/httpUtils"

// export type UserLogInPayload = {
//     username: string
//     password: string
// }

// export type UserTokenPayload = {
//     token: string
// }


// const rootUri = '/api/security/'

// export const login = (loginPayload: UserLogInPayload): Promise<HttpResponse<UserTokenPayload>> => {
//     return callMashupMediaApi<UserTokenPayload>(HttpMethod.POST, rootUri + 'login', JSON.stringify(loginPayload))
// }