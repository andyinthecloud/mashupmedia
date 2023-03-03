import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { callMashupMediaApi, callMashupMediaApiNoRedirect, HttpMethod, HttpResponse } from "../../../common/utils/httpUtils"
import { SecureMediaPayload } from "../../rest/secureMediaPayload"
import { AlbumPayload, ArtistPayload, TrackPayload } from "./musicCalls"

export enum NavigatePlaylistType {
    PREVIOUS = "PREVIOUS",
    CURRENT = "CURRENT",
    NEXT = "NEXT"
}

export type NavigatePlaylistPayload = {
    navigatePlaylistType?: NavigatePlaylistType
    mediaItemId?: number
}

export type PlaylistPayload = {
    id: number
    name: string
}

export type MusicPlaylistTrackPayload = {
    trackPayload: TrackPayload
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    playlistPayload: PlaylistPayload
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

export const hasPlaylist = (userToken: string | undefined): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApiNoRedirect<ServerResponsePayload<string>>(HttpMethod.GET, playlistUrl + "/initialised", userToken)
}

export const trackProgress = (playlistId: number, progress: number, userToken: string | undefined): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.GET, `${playlistUrl}/progress/${playlistId}?progress=${Math.round(progress)}`, userToken)
}

export const getPlaylistTracks = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
    return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.GET, `${playlistUrl}/tracks/${playlistId}`, userToken)
}