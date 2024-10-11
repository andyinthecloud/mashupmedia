import { ServerResponsePayload } from "../../../common/utils/formValidationUtils"
import { backEndUrl, callMashupMediaApi, HttpMethod, HttpResponse, timestamp } from '../../../common/utils/httpUtils'
import { NameValuePayload } from "../../../configuration/backend/metaCalls"
import { UserPayload } from "../../../configuration/backend/userCalls"
import { ExternalLinkPayload } from "../../rest/mediaCalls"
import { SecureMediaPayload } from "../../rest/secureMediaPayload"
import { MetaImagePayload } from "./musicUploadCalls"

export type ArtistPayload = {
    id: number
    name: string
    profile?: string
    userPayload?: UserPayload
    externalLinkPayloads?: ExternalLinkPayload[]
    metaImagePayloads?: MetaImagePayload[]
}

export type CreateAlbumPayload = {
    name: string
    artistId: number
}

export type CreateArtistPayload = {
    name: string
}

export type AlbumPayload = {
    id: number
    name: string
    summary?: string
    externalLinkPayloads?: ExternalLinkPayload[]
    metaImagePayloads?: MetaImagePayload[]
}

export type SaveAlbumPayload = AlbumPayload & {
    artistId: number
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
    year: number
}

export type AlbumWithTracksAndArtistPayload = {
    artistPayload: ArtistPayload
    albumPayload: AlbumPayload
    trackPayloads: TrackPayload[]
}

export type ArtistWithAlbumsPayload = {
    artistPayload: ArtistPayload
    albumPayloads: AlbumPayload[]
}

export enum ImageType {
    ORIGINAL = "ORIGINAL", THUMBNAIL = "THUMBNAIL"
}

const musicUri = "/api/private/music"
const artistUri = musicUri + "/artists"
const albumUrl = musicUri + "/albums"

export const albumArtImageUrl = (albumId: number, imageType: ImageType, mediaToken: string, id?: number): string => {
    return `${backEndUrl('/stream/secure/music/album-art')}/${albumId}?mediaToken=${mediaToken}&imageType=${imageType}&id=${id || ''}`
}

export const artistImageUrl = (artistId: number, imageType: ImageType, mediaToken: string, id?: number): string => {
    return `${backEndUrl('/stream/secure/music/artist-art')}/${artistId}?mediaToken=${mediaToken}&imageType=${imageType}&id=${id || ''}`
}

export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]>(HttpMethod.GET, artistUri, userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<ArtistWithAlbumsPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<ArtistWithAlbumsPayload>>(HttpMethod.GET, artistUri + "/" + artistId, userToken)
}

export const getArtistListItem = (artistId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<NameValuePayload<number>>>> => {
    return callMashupMediaApi<SecureMediaPayload<NameValuePayload<number>>>(HttpMethod.GET, artistUri + "/" + artistId, userToken)
}

export const createArtist = (createArtistPayload: CreateArtistPayload, userToken?: string): Promise<HttpResponse<ArtistPayload>> => {
    return callMashupMediaApi<ArtistPayload>(HttpMethod.POST, artistUri, userToken, JSON.stringify(createArtistPayload))
}

export const saveArtist = (artistPayload: ArtistPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<boolean>>> => {
    return callMashupMediaApi<ServerResponsePayload<boolean>>(HttpMethod.PUT, artistUri, userToken, JSON.stringify(artistPayload))
}

export const deleteArtist = (artistId: number, userToken?: string): Promise<HttpResponse<NameValuePayload<string>>> => {
    return callMashupMediaApi<NameValuePayload<string>>(HttpMethod.DELETE, artistUri + "/" + artistId, userToken)
}

export const getAlbums = (userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithArtistPayload>[]>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithArtistPayload>[]>(HttpMethod.GET, albumUrl, userToken)
}

export const getAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>(HttpMethod.GET, albumUrl + "/" + albumId, userToken)
}

export const createAlbum = (createAlbumPayload: CreateAlbumPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<AlbumPayload>>> => {
    return callMashupMediaApi<ServerResponsePayload<AlbumPayload>>(HttpMethod.POST, albumUrl, userToken, JSON.stringify(createAlbumPayload))
}

export const mediaStreamUrl = (mediaItemId: number, mediaToken: string, seconds?: number): string => {
    const timeFragment = seconds ? `#t=${seconds}` : ''
    return `${backEndUrl('/stream/secure/media')}/${mediaItemId}?mediaToken=${mediaToken}${timeFragment}`
}

export const playlistStreamUrl = (playlistId: number, mediaToken: string, seconds?: number): string => {
    const timeFragment = seconds ? `#t=${seconds}` : ''
    return `${backEndUrl('/stream/secure/playlist')}/${playlistId}?timestamp=${timestamp()}&mediaToken=${mediaToken}${timeFragment}`
}

export const deleteAlbum = (albumId: number, userToken?: string): Promise<HttpResponse<ServerResponsePayload<boolean>>> => {
    return callMashupMediaApi<ServerResponsePayload<boolean>>(HttpMethod.DELETE, albumUrl + "/" + albumId, userToken)
}

export const saveAlbum = (saveAlbumPayload: SaveAlbumPayload, userToken?: string): Promise<HttpResponse<ServerResponsePayload<boolean>>> => {
    return callMashupMediaApi<ServerResponsePayload<boolean>>(HttpMethod.PUT, albumUrl, userToken, JSON.stringify(saveAlbumPayload))
}