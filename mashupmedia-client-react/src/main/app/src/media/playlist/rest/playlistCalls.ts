import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../../common/utils/httpUtils"
import { PlaylistPayload, PlaylistWithMediaItemsPayload } from "../../music/rest/playlistActionCalls"

const playlistUrl = "/api/playlist"

export const getPlaylists = (userToken?: string): Promise<HttpResponse<PlaylistPayload[]>> => {
    return callMashupMediaApi<PlaylistPayload[]>(HttpMethod.GET, playlistUrl, userToken,)
}

export const updatePlaylist = (playlistWithMediaItemsPayload: PlaylistWithMediaItemsPayload, userToken: string | undefined): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.PUT, playlistUrl, userToken, JSON.stringify(playlistWithMediaItemsPayload))
}

export const deletePlaylist = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>> (HttpMethod.DELETE, `${playlistUrl}/${playlistId}`, userToken)
}
