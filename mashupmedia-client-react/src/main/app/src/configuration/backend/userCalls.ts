import { ServerResponsePayload } from "../../common/utils/formValidationUtils";
import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils"
import { CreateUserPayload } from "./createUserCalls";
import { NameValuePayload } from "./metaCalls";

export type UserPayload = {
    enabled: boolean
    administrator: boolean
    username: string
    password?: string
    name: string
    createdOn?: string | null
    updatedOn?: string | null
    rolePayloads?: NameValuePayload<string>[]
    exists: boolean
    validated: boolean
}

const userUri = '/api/private/admin/user/'

export const userAccount = (userName: string, userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload>(HttpMethod.GET, userUri + 'account/' + encodeURIComponent(userName), userToken)
}

export const createAccount = (createUserPayload: CreateUserPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.POST, userUri + 'account', userToken, JSON.stringify(createUserPayload))
}

export const saveUserAccount = (userPayload: UserPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, userUri + 'account', userToken, JSON.stringify(userPayload))
}

export const getMyAccount = (userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload>(HttpMethod.GET, userUri + 'my-account', userToken)
}

export const getUsers = (userToken?: string): Promise<HttpResponse<UserPayload[]>> => {
    return callMashupMediaApi<UserPayload[]>(HttpMethod.GET, userUri + 'all', userToken)
}

export const deleteUserAccount = (username: string, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean>(HttpMethod.DELETE, userUri + 'delete-account', userToken, username)
}


