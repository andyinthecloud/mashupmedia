import { backEndUrl, callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'
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

export type TrackPayload = {
    id: number
    name: string
    totalSeconds: number
    minutes?: number
    seconds?: number
}

export type AlbumWithTracksAndArtistPayload = {
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    trackPayloads: TrackPayload[]
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
    return `${backEndUrl('/stream/secure/music/album-art')}/${albumId}?mediaToken=${mediaToken}&imageType=${imageType}`
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

export const getAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>(HttpMethod.GET, albumUrl + "/" + albumId, userToken)
}

export const mediaStreamUrl = (mediaItemId: number, mediaToken: string, seconds?: number): string => {
    const timeFragment = seconds ? `#t=${seconds}` : ''
    return `${backEndUrl('/stream/secure/media')}/${mediaItemId}?mediaToken=${mediaToken}${timeFragment}`
}

