import { ServerResponsePayload } from "../../common/utils/formValidationUtils"
import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"

export type CreateUserPayload = {
    username: string
    password?: string
    name: string
    activationCode?: string
}

const userUri = '/api/public/create-user/'

export const stepActivateUser = (createUserPayload: CreateUserPayload): Promise<HttpResponse<ServerResponsePayload<CreateUserPayload>>> => {
    return callMashupMediaApi<ServerResponsePayload<CreateUserPayload>> (HttpMethod.POST, userUri + 'step-activate', undefined, JSON.stringify(createUserPayload))
}

export const stepCreateUser = (createUserPayload: CreateUserPayload): Promise<HttpResponse<ServerResponsePayload<CreateUserPayload>>> => {
    return callMashupMediaApi<ServerResponsePayload<CreateUserPayload>> (HttpMethod.POST, userUri + 'step-create', undefined, JSON.stringify(createUserPayload))
}