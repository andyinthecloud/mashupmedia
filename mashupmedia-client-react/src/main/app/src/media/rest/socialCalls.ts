import { ServerResponsePayload } from "../../common/utils/formValidationUtils"
import { callMashupMediaApi, HttpMethod, HttpResponse } from "../../common/utils/httpUtils"

export type VoteMediaItemPayload = {
    mediaItemId: number
}

const voteUri = "/api/private/vote"

export const voteMediaItem = (voteMediaItemPayload: VoteMediaItemPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<boolean>>> => {
    return callMashupMediaApi<ServerResponsePayload<boolean>>(HttpMethod.POST, voteUri + "/media-item", userToken, JSON.stringify(voteMediaItemPayload))
}
