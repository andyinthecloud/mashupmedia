import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { backendUrl, callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'
import { SecureMediaPayload } from "../../rest/secureMediaPayload"

export type ArtistPayload = {
    id: number
    name: string
    indexLetter: string
}

export type AlbumPayload = {
    id: number
    name: string
}

export type AlbumWithArtistPayload = {
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
}

export type SongPayload = {
    id: number
    name: string
    minutes?: number
    seconds?: number
}

export type AlbumWithSongsAndArtistPayload = {
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    songPayloads: SongPayload[]
}

export type ArtistWithAlbumsPayload = {
    mediaToken: string
    artistPayload: ArtistPayload
    albumPayloads: AlbumPayload[]
}

export enum ImageType {
    ORIGINAL = "ORIGINAL", THUMBNAIL = "THUMBNAIL"
}

const musicUri = "/api/music"
const artistUri = musicUri + "/artists"
const albumUrl = musicUri + "/albums"

export const albumArtImageUrl = (albumId: number, imageType: ImageType, mediaToken: string): string => {
    return `${backendUrl('/media/music/album-art')}/${albumId}?mediaToken=${mediaToken}&imageType=${imageType}`
}

export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]>(HttpMethod.GET, artistUri + "/", userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<ArtistWithAlbumsPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<ArtistWithAlbumsPayload>>(HttpMethod.GET, artistUri + "/" + artistId, userToken)
}

export const getAlbums = (userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithArtistPayload>[]>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithArtistPayload>[]>(HttpMethod.GET, albumUrl + "/", userToken)
}

export const getAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithSongsAndArtistPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithSongsAndArtistPayload>>(HttpMethod.GET, albumUrl + "/" + albumId, userToken)
}

// playlist calls

const playlistUrl = "/api/playlist/music"


export const playAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, playlistUrl + "/album", userToken, JSON.stringify(albumId))    
}

export const addAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<string>>> => {
    return callMashupMediaApi<ServerResponsePayload<string>>(HttpMethod.PUT, playlistUrl + "/album", userToken, JSON.stringify(albumId))    
}
