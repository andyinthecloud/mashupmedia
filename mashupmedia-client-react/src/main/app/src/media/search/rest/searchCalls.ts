import { PagePayload } from "../../../common/payload/container"
import { HttpMethod, HttpResponse, callMashupMediaApi, objectToQueryParameters } from "../../../common/utils/httpUtils"
import { NameValuePayload } from "../../../configuration/backend/metaCalls"
import { AlbumPayload, ArtistPayload, TrackPayload } from "../../music/rest/musicCalls"
import { MashupMediaType } from "../../music/rest/playlistActionCalls"

const searchUri = "/api/search"
const searchMediaUri = searchUri + "/media"

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

export enum SortType {
    ASC = 'ASC',
    DESC = 'DESC'
}
``
export type MediaItemSearchCriteriaPayload = {
    searchText?: string
    genreIdNames?: string[]
    decades?: number[]
    orderBy?: string
    sortBy?: SortType
    mashupMediaType?: MashupMediaType
}

export const searchMedia = (mediaItemSearchCriteriaPayload: MediaItemSearchCriteriaPayload, userToken?: string): Promise<HttpResponse<PagePayload<MediaSearchResultPayload>>> => {
    console.log("searchMedia", mediaItemSearchCriteriaPayload)
    // mediaItemSearchCriteriaPayload.orderBy = mediaItemSearchCriteriaPayload.orderBy + '_' + mediaItemSearchCriteriaPayload

    const payload: MediaItemSearchCriteriaPayload = {
        mashupMediaType: mediaItemSearchCriteriaPayload.mashupMediaType,
        searchText: mediaItemSearchCriteriaPayload.searchText,
        decades: mediaItemSearchCriteriaPayload.decades,
        genreIdNames: mediaItemSearchCriteriaPayload.genreIdNames,
        orderBy: orderBy(mediaItemSearchCriteriaPayload?.orderBy || '', mediaItemSearchCriteriaPayload?.sortBy || '')
    }

    return callMashupMediaApi<PagePayload<MediaSearchResultPayload>>(
        HttpMethod.GET,
        `${searchMediaUri}${objectToQueryParameters(payload)}`,
        userToken
    )
}

const orderBy = (orderBy: string, sortBy: string): string => {
    if (!orderBy) {
        return ''
    }

    return orderBy + '_' + sortBy || SortType.ASC


}

export const getGenres = (userToken?: string): Promise<HttpResponse<GenrePayload[]>> => {
    return callMashupMediaApi<GenrePayload[]>(HttpMethod.GET, `${searchMediaUri}/genres`, userToken)
}


export const getOrderByNames = (userToken?: string): Promise<HttpResponse<NameValuePayload<string>[]>> => {
    return callMashupMediaApi<NameValuePayload<string>[]>(HttpMethod.GET, `${searchMediaUri}/orderByNames`, userToken)
}