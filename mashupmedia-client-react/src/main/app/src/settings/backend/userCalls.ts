import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../utils/httpUtils"
import { NameValuePayload } from "./metaCalls";

// export{}

export type UserPayload = {
    admin: boolean;
    enabled: boolean;
    editable: boolean;
    username: string;
    name: string;
    createdOn?: string | null;
    updatedOn?: string | null;
    rolePayloads?: NameValuePayload<string>[];
    groupPayloads?: NameValuePayload<number>[];

}

const userUri = '/api/admin/user/'

export const fetchMyAccount = (userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload> (HttpMethod.GET, userUri + 'me', userToken)
}

export const saveMyAccount = (userPayload: UserPayload, userToken?: string): Promise<HttpResponse<UserPayload>> => {
    return callMashupMediaApi<UserPayload> (HttpMethod.PUT, userUri + 'me', userToken, JSON.stringify(userPayload))
}