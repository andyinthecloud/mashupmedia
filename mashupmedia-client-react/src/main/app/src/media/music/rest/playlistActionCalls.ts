import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { callMashupMediaApi, callMashupMediaApiNoRedirect, HttpMethod, HttpResponse } from "../../../common/utils/httpUtils"
import { SecureMediaPayload } from "../../rest/secureMediaPayload"
import { AlbumPayload, ArtistPayload, TrackPayload } from "./musicCalls"

export enum NavigatePlaylistType {
    PREVIOUS = "PREVIOUS",
    CURRENT = "CURRENT",
    NEXT = "NEXT"
}

export enum MashupMediaType {
    MUSIC = "MUSIC", 
    VIDEO = "VIDEO", 
    PHOTO = "PHOTO"
}

export type NavigatePlaylistPayload = {
    navigatePlaylistType?: NavigatePlaylistType
    playlistMediaItemId?: number
}

export type PlaylistPayload = {
    id: number
    name: string
    mashupMediaType: MashupMediaType
}

export type MusicPlaylistTrackPayload = {
    id: number
    trackPayload: TrackPayload
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    playlistPayload: PlaylistPayload
    first: boolean
    last: boolean
    encoderStatusType: EncoderStatusType
}

export enum PlaylistActionTypePayload {
    NONE = "NONE",
    REMOVE_ITEMS = "REMOVE_ITEMS",
    MOVE_TOP = "MOVE_TOP",
    MOVE_BOTTOM = "MOVE_BOTTOM"
}


export enum EncoderStatusType {
    OK = "OK",
    ENODER_NOT_INSTALLED = "ENODER_NOT_INSTALLED",
    SENT_FOR_ENCODING = "SENT_FOR_ENCODING",
    ERROR = "ERROR"
}


export type PlaylistActionPayload = {
    playlistActionTypePayload: PlaylistActionTypePayload
    playlistId: number
    playlistMediaItemIds: number[]
}

export type PlaylistMediaItemPayload = {
    playlistMediaItemId: number
}

export type PlaylistTrackPayload = PlaylistMediaItemPayload & {
    artistPayload: ArtistPayload
    trackPayload: TrackPayload
}

export interface PlaylistWithMediaItemsPayload {
    mashupMediaType: MashupMediaType
    playlistPayload: PlaylistPayload
    playlistMediaItemPayloads: PlaylistMediaItemPayload[]
}

export interface PlaylistWithTracksPayload extends PlaylistWithMediaItemsPayload {
    playlistMediaItemPayloads: PlaylistTrackPayload[]
}

const playlistUrl = "/api/playlist"
const musicPlaylistUrl = playlistUrl + "/music"

export const playTrack = (trackId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<EncoderStatusType>>> => {
    return callMashupMediaApi<ServerResponsePayload<EncoderStatusType>>(HttpMethod.PUT, musicPlaylistUrl + "/play-track", userToken, '' + trackId)
}

export const addTrack = (trackId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<EncoderStatusType>>> => {
    return callMashupMediaApi<ServerResponsePayload<EncoderStatusType>>(HttpMethod.PUT, musicPlaylistUrl + "/add-track", userToken, '' + trackId)
}

export const playAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<EncoderStatusType>>> => {
    return callMashupMediaApi<ServerResponsePayload<EncoderStatusType>>(HttpMethod.PUT, musicPlaylistUrl + "/play-album", userToken, '' + albumId)
}

export const addAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<EncoderStatusType>>> => {
    return callMashupMediaApi<ServerResponsePayload<EncoderStatusType>>(HttpMethod.PUT, musicPlaylistUrl + "/add-album", userToken, '' + albumId)
}

export const navigateTrack = (navigatePlaylistPayload: NavigatePlaylistPayload, userToken?: string): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.PUT, musicPlaylistUrl + "/navigate", userToken, JSON.stringify(navigatePlaylistPayload))
}

export const currentTrack = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<SecureMediaPayload<MusicPlaylistTrackPayload>>> => {
    return callMashupMediaApiNoRedirect<SecureMediaPayload<MusicPlaylistTrackPayload>>(HttpMethod.GET, `${musicPlaylistUrl}/current/${playlistId}`, userToken)
}

export const getPlaylist = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<PlaylistWithMediaItemsPayload>> => {
    return callMashupMediaApi<PlaylistWithMediaItemsPayload> (HttpMethod.GET, `${playlistUrl}/${playlistId}`, userToken)
}


// export const getPlaylistTracks = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
//     return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.GET, `${musicPlaylistUrl}/tracks/${playlistId}`, userToken)
// }

// export const updatePlaylist = (playlistActionPayload: PlaylistActionPayload, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
//     return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.PUT, playlistUrl, userToken, JSON.stringify(playlistActionPayload))
// }

// export const createPlaylist = (playlistPayload: PlaylistPayload, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
//     return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.POST, playlistUrl, userToken, JSON.stringify(playlistPayload))
// }

// export const deletePlaylist = (playlistId: number, userToken: string | undefined): Promise<HttpResponse<MusicPlaylistTrackPayload[]>> => {
//     return callMashupMediaApiNoRedirect<MusicPlaylistTrackPayload[]> (HttpMethod.DELETE, `${playlistUrl}/${playlistId}`, userToken)
// }