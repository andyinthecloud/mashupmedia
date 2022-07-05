import {  callMashupMediaApi, HttpMethod, HttpResponse } from "../../utils/httpUtils";


export type NameValuePayload<T> = {
    name: string;    
    value: T
}

export const fetchGroupPayloads = (userToken?: string): Promise<HttpResponse<NameValuePayload<number>[]>> => {
    return callMashupMediaApi<NameValuePayload<number>[]> (HttpMethod.GET, '/api/meta/groups', userToken)
}

export const fetchRolePayloads = async (userToken?: string): Promise<HttpResponse<NameValuePayload<string>[]>> => {
    return callMashupMediaApi<NameValuePayload<string>[]> (HttpMethod.GET, '/api/meta/roles', userToken)
}