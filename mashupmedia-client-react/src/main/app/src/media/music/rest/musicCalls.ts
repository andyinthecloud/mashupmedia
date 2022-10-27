import { callMashupMediaApi, HttpMethod, HttpResponse } from '../../../common/utils/httpUtils'

export type ArtistPayload = {
    id: number
    name: string
    indexLetter: string
}

export type AlbumPayload = {
    id: number
    name: string
    albumArtImagePayload: AlbumArtImagePayload
}

export type AlbumArtImagePayload = {
    name: string
    url: string
    thumbnailUrl: string
    contentType: string
}

export type ArtistWithAlbumsPayload = {
    artistPayload: ArtistPayload
    albumPayloads: AlbumPayload[] 

}


const musicUri = '/api/music/artists/'

export const getArtists = (userToken?: string): Promise<HttpResponse<ArtistPayload[]>> => {
    return callMashupMediaApi<ArtistPayload[]> (HttpMethod.GET, musicUri, userToken)
}

export const getArtist = (artistId: number, userToken?: string): Promise<HttpResponse<ArtistWithAlbumsPayload>> => {
    return callMashupMediaApi<ArtistWithAlbumsPayload> (HttpMethod.GET, musicUri + artistId, userToken)
}
