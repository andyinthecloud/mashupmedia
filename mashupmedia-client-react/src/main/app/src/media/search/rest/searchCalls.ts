import { HttpMethod, HttpResponse, callMashupMediaApi, objectToQueryParameters } from "../../../common/utils/httpUtils"
import { AlbumPayload, ArtistPayload, TrackPayload } from "../../music/rest/musicCalls"
import { MashupMediaType } from "../../music/rest/playlistActionCalls"

const searchUri = "/api/search"
const searchMediaUri = searchUri + "/media"

// export type MediaSearchPayload = {
//     text: string
//     genres: string[]
//     decades: number[]
// }

export type GenrePayload = {
    id: number
    idName: string
    name: string
}


export type MediaSearchResultPayload = {
    mashupMediaType: MashupMediaType
}

export type MusicSearchResultPayload = MediaSearchResultPayload & {
    trackPayload: TrackPayload
    albumPayload: AlbumPayload
    artistPayload: ArtistPayload
}

export type MediaItemSearchCriteriaPayload = {
    searchText?: string
    genreIdNames?: string[]
    decades?: number[];
}

export const searchMedia = (mediaItemSearchCriteriaPayload: MediaItemSearchCriteriaPayload, userToken?: string): Promise<HttpResponse<MediaSearchResultPayload[]>> => {
    console.log("searchMedia", mediaItemSearchCriteriaPayload)
    
    return callMashupMediaApi<MediaSearchResultPayload[]>(
        HttpMethod.GET,
        `${searchMediaUri}${objectToQueryParameters(mediaItemSearchCriteriaPayload)}`,
        userToken
    )
}

export const getGenres = (userToken?: string): Promise<HttpResponse<GenrePayload[]>> => {
    return callMashupMediaApi<GenrePayload[]>(HttpMethod.GET, `${searchMediaUri}/genres`, userToken)
}