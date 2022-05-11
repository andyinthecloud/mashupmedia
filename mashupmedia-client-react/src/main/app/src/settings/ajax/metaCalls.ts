import {  fetchMashupMediaApi, HttpMethod, HttpResponse } from "../../utils/httpUtils";

export type RolePayload = {
    idName: string;
    name: string;    
}

export type GroupPayload = {
    id: number;
    name: string;    
}

export const fetchGroupPayloads = (userToken?: string): Promise<HttpResponse<GroupPayload[]>> => {
    return fetchMashupMediaApi<GroupPayload[]> (HttpMethod.GET, '/api/meta/groups', userToken)
}

export const fetchRolePayloads = async (userToken?: string): Promise<HttpResponse<RolePayload[]>> => {
    return fetchMashupMediaApi<RolePayload[]> (HttpMethod.GET, '/api/meta/roles', userToken)
}