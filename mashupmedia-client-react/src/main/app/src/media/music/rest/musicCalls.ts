import { callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'
import { SecureTokenPayload } from '../../rest/secureTokenPayload'

export type ArtistPayload = {
    id: number
    name: string
    indexLetter: string
}

export type AlbumPayload = {
    id: number
    name: string
}

export type ArtistWithAlbumsPayload = {
    secureToken: string
    artistPayload: ArtistPayload
    albumPayloads: AlbumPayload[]
}

const musicUri = '/api/music/artists/'

export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]>(HttpMethod.GET, musicUri, userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<SecureTokenPayload<ArtistWithAlbumsPayload>>> => {
    return callMashupMediaApi<SecureTokenPayload<ArtistWithAlbumsPayload>>(HttpMethod.GET, musicUri + artistId, userToken)
}
