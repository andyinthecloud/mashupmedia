import { ServerResponsePayload } from "../../common/utils/formValidationUtils"
import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../common/utils/httpUtils"
import { NameValuePayload } from "./metaCalls"

const encodeUri = "/api/private/encode"

export const getFfmpegInstallation = (userToken?: string): Promise<HttpResponse<NameValuePayload<string>>> => {
    return callMashupMediaApi<NameValuePayload<string>>(HttpMethod.GET, encodeUri + "/", userToken)
}

export const saveFfmpegInstallation = (ffmpegPayload: NameValuePayload<string>, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.POST, encodeUri + "/", userToken, JSON.stringify(ffmpegPayload))
}

export const verifyFfmpegInstallation = (pathToFfmpeg: string, userToken?: string): Promise<HttpResponse<boolean>> => {
    return callMashupMediaApi<boolean>(HttpMethod.GET, `${encodeUri}/verify-installation?path=${encodeURIComponent(pathToFfmpeg)}`, userToken)
}