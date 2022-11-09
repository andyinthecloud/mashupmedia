import { callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'
import { MediaTokenPayload as MediaTokenPayload } from '../../rest/secureTokenPayload'

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


export type ArtistWithAlbumsPayload = {
    mediaToken: string
    artistPayload: ArtistPayload
    albumPayloads: AlbumPayload[]
}

const musicUri = '/api/music/artists/'

export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]>(HttpMethod.GET, musicUri, userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<MediaTokenPayload<ArtistWithAlbumsPayload>>> => {
    return callMashupMediaApi<MediaTokenPayload<ArtistWithAlbumsPayload>>(HttpMethod.GET, musicUri + artistId, userToken)
}
