import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { callMashupMediaApi, callMashupMediaApiNoRedirect, HttpMethod, HttpResponse } from "../../../common/utils/httpUtils"
import { SecureMediaPayload } from "../../rest/secureMediaPayload"
import { ArtistPayload, TrackPayload } from "./musicCalls"

export enum NavigatePlaylistType {
    PREVIOUS = "PREVIOUS",
    CURRENT = "CURRENT",
    NEXT = "NEXT"
}

export type NavigatePlaylistPayload = {
    navigatePlaylistType?: NavigatePlaylistType
    mediaItemId?: number
}

export type MusicPlaylistTrackPayload = {
    trackPayload: TrackPayload
    artistPayload: ArtistPayload
    first: boolean
    last: boolean
}

const playlistUrl = "/api/playlist/music"

export const playAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, playlistUrl + "/play-album", userToken, '' + albumId)
}

export const addAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, playlistUrl + "/add-album", userToken, '' + albumId)
}

export const navigateTrack = (navigatePlaylistPayload: NavigatePlaylistPayload, userToken?: string): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.PUT, playlistUrl + "/navigate", userToken, JSON.stringify(navigatePlaylistPayload))
}