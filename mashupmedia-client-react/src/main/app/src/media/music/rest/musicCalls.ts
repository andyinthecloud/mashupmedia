import { callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'
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

const musicUri = "/api/music"
const artistUri = musicUri + "/artists"
const albumUri = musicUri + "/albums"


export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]>(HttpMethod.GET, artistUri + "/", userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<SecureMediaPayload<ArtistWithAlbumsPayload>>> => {
    return callMashupMediaApi<SecureMediaPayload<ArtistWithAlbumsPayload>>(HttpMethod.GET, artistUri + "/" + artistId, userToken)
}

export const getRandomAlbums = (userToken?: string): Promise<HttpResponse<SecureMediaPayload<AlbumWithArtistPayload>[]>> => {
    return callMashupMediaApi<SecureMediaPayload<AlbumWithArtistPayload>[]>(HttpMethod.GET, albumUri + '/random', userToken)
}