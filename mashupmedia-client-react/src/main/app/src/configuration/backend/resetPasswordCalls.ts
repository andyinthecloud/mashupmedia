import { ServerResponsePayload } from "../../common/utils/formValidationUtils"
import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"

export type ResetPasswordPayload = {
    username: string
    password: string
    token?: string
    activationCode?: string
}

const publicResetPasswordUri = '/api/public/reset-password/'
const privateResetPasswordUri = '/api/private/admin/user/'

export const resetPassword = (resetPasswordPayload: ResetPasswordPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, privateResetPasswordUri + 'reset-password', userToken, JSON.stringify(resetPasswordPayload))
}

export const stepResetPassword = (resetPasswordPayload: ResetPasswordPayload): Promise<HttpResponse<ServerResponsePayload<ResetPasswordPayload>>> => {
    console.log('stepResetPassword')
    return callMashupMediaApi<ServerResponsePayload<ResetPasswordPayload>>(HttpMethod.PUT, publicResetPasswordUri + 'step-reset', undefined, JSON.stringify(resetPasswordPayload))
}

export const stepActivatePassword = (resetPasswordPayload: ResetPasswordPayload): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, publicResetPasswordUri + 'step-activate', undefined, JSON.stringify(resetPasswordPayload))
}

