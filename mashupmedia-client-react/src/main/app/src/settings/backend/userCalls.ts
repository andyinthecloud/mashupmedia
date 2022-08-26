import { ServerResponsePayload } from "../../utils/form-validation-utils";
import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../utils/httpUtils"
import { NameValuePayload } from "./metaCalls";

export type UserPayload = {
    enabled: boolean
    editable: boolean
    administrator: boolean
    username: string
    name: string
    createdOn?: string | null
    updatedOn?: string | null
    rolePayloads?: NameValuePayload<string>[]
    groupPayloads?: NameValuePayload<number>[]
}

export type ChangeUserPasswordPayload = {
    username?: string
    currentPassword: string
    newPassword: string
    confirmPassword: string
}

const userUri = '/api/admin/user/'

export const userAccount = (userName: string, userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload> (HttpMethod.GET, userUri + 'account/' + encodeURIComponent(userName), userToken)
}

export const saveUserAccount = (userPayload: UserPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.PUT, userUri + 'account', userToken, JSON.stringify(userPayload))
}

export const myAccount = (userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload> (HttpMethod.GET, userUri + 'my-account', userToken)
}

export const changePassword = (changeUserPasswordPayload: ChangeUserPasswordPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.PUT, userUri + 'change-password', userToken, JSON.stringify(changeUserPasswordPayload))
}

export const users = (userToken?: string): Promise<HttpResponse<UserPayload[]>> => {
    return callMashupMediaApi<UserPayload[]> (HttpMethod.GET, userUri + 'all', userToken)
}

export const deleteUserAccount = (username: string, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean> (HttpMethod.DELETE, userUri + 'delete-account', userToken, username)
}

