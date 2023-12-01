import { ServerResponsePayload } from "../../common/utils/formValidationUtils";
import {  callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils";

export type NameValuePayload<T> = {
    name: string    
    value: T
}

const metaUri = '/api/private/meta/'

export const getGroups = (userToken?: string): Promise<HttpResponse<NameValuePayload<number>[]>> => {
    return callMashupMediaApi<NameValuePayload<number>[]> (HttpMethod.GET, metaUri + 'groups', userToken)
}

export const getRoles = async (userToken?: string): Promise<HttpResponse<NameValuePayload<string>[]>> => {
    return callMashupMediaApi<NameValuePayload<string>[]> (HttpMethod.GET, metaUri + 'roles', userToken)
}

export const getGroup = (groupId: number, userToken?: string): Promise<HttpResponse<NameValuePayload<number>>> => {
    return callMashupMediaApi<NameValuePayload<number>> (HttpMethod.GET, metaUri + 'group/' + groupId, userToken)
}

export const saveGroup = (groupPayload: NameValuePayload<number>, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.PUT, metaUri + 'group', userToken, JSON.stringify(groupPayload))
}

export const deleteGroup = (groupId: number, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean> (HttpMethod.DELETE, metaUri + 'group', userToken, ''+groupId)
}