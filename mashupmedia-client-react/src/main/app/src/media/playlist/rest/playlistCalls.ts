import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../../common/utils/httpUtils"
import { NameValuePayload } from "../../../configuration/backend/metaCalls"
import { PlaylistPayload } from "../../music/rest/playlistActionCalls"

const playlistUrl = "/api/playlist"

export const getPlaylists = (userToken?: string): Promise<HttpResponse<PlaylistPayload[]>> => {
    return callMashupMediaApi<PlaylistPayload[]>(HttpMethod.GET, playlistUrl, userToken,)
}