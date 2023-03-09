import { number } from "prop-types"
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
    id: number
    trackPayload: TrackPayload
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    playlistPayload: PlaylistPayload
    first: boolean
    last: boolean
}

export enum PlaylistActionTypePayload {
    NONE = "NONE",
    REMOVE_ITEMS = "REMOVE_ITEMS",
    MOVE_TOP = "MOVE_TOP",
    MOVE_BOTTOM = "MOVE_BOTTOM"
}

export type PlaylistActionPayload = {
    playlistActionTypePayload: PlaylistActionTypePayload
    playlistId: number
    playlistMediaItemIds: number[]
}

export type CreatePlaylistPayload = {
    name: string
}

const playlistUrl = "/api/playlist"
const musicPlaylistUrl = playlistUrl + "/music"

export const playAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, musicPlaylistUrl + "/play-album", userToken, '' + albumId)
}

export const addAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, musicPlaylistUrl + "/add-album", userToken, '' + albumId)
}

export const navigateTrack = (navigatePlaylistPayload: NavigatePlaylistPayload, userToken?: string): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.PUT, musicPlaylistUrl + "/navigate", userToken, JSON.stringify(navigatePlaylistPayload))
}

export const hasPlaylist = (userToken: string | undefined): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApiNoRedirect<ServerResponsePayload<string>>(HttpMethod.GET, musicPlaylistUrl + "/initialised", userToken)
}

export const trackProgress = (playlistId: number, progress: number, userToken: string | undefined): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.GET, `${musicPlaylistUrl}/progress/${playlistId}?progress=${Math.round(progress)}`, userToken)
}

export const getPlaylistTracks = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
    return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.GET, `${musicPlaylistUrl}/tracks/${playlistId}`, userToken)
}

export const updatePlaylist = (playlistActionPayload: PlaylistActionPayload, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
    return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.PUT, playlistUrl, userToken, JSON.stringify(playlistActionPayload))
}

export const createPlaylist = (createPlaylistPayload: CreatePlaylistPayload, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
    return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.POST, playlistUrl, userToken, JSON.stringify(createPlaylistPayload))
}

export const deletePlaylist = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
    return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.DELETE, `${playlistUrl}/${playlistId}`, userToken)
}