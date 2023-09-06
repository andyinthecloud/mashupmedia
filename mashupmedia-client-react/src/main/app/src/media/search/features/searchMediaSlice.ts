import { createAsyncThunk, createSlice } from "@reduxjs/toolkit"
import { PagePayload } from "../../../common/payload/commonPayload"
import { PayloadState } from "../../../common/redux/store"
import { HttpMethod, backEndUrl, objectToQueryParameters, restHeaders } from "../../../common/utils/httpUtils"
import { AlbumPayload, ArtistPayload, TrackPayload } from "../../music/rest/musicCalls"
import { MashupMediaType } from "../../music/rest/playlistActionCalls"
import { securityToken } from "../../../common/security/securityUtils"
import { UserTokenPayload } from "../../../common/security/features/securitySlice"

export enum SortType {
    ASC = 'ASC',
    DESC = 'DESC'
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
    decades?: number[]
    orderBy?: string
    sortBy?: SortType
    mashupMediaType?: MashupMediaType
    pageNumber?: number
}

export type SearchMediaCriteriaPayload = UserTokenPayload & {
    mediaItemSearchCriteriaPayload: MediaItemSearchCriteriaPayload
}

export type MediaSearchResultsPayload = {
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    pagePayload?: PagePayload<MediaSearchResultPayload>
}



const initialState: PayloadState<MediaSearchResultsPayload> = {
    payload: null,
    error: null,
    loading: false
}

const searchUri = "/api/search"
const searchMediaUri = searchUri + "/media"

export const searchMedia = createAsyncThunk<MediaSearchResultsPayload, SearchMediaCriteriaPayload>(
    'searchMedia',
    async (searchMediaCriteriaPayload: SearchMediaCriteriaPayload) => {

        const userToken = securityToken(searchMediaCriteriaPayload.token)
        const preparedSearchMediaCriteriaPayload: MediaItemSearchCriteriaPayload = {
            mashupMediaType: searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload.mashupMediaType,
            searchText: searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload.searchText,
            decades: searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload.decades,
            genreIdNames: searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload.genreIdNames,
            orderBy: orderBy(searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload?.orderBy || '', searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload?.sortBy || ''),
            pageNumber: searchMediaCriteriaPayload.mediaItemSearchCriteriaPayload.pageNumber
        }

        const url: string = backEndUrl(searchMediaUri)
        const response = await fetch(`${url}${objectToQueryParameters(preparedSearchMediaCriteriaPayload)}`, {
            method: HttpMethod.GET,
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(userToken)
        })        

        return {
            mediaItemSearchCriteriaPayload: {...preparedSearchMediaCriteriaPayload},
            pagePayload: (await response.json()) as PagePayload<MediaSearchResultPayload> 
        }
    }
)

const orderBy = (orderBy: string, sortBy: string): string => {
    return orderBy
        ? orderBy + '_' + sortBy || SortType.ASC
        : ''
}

export const mediaSearchResultsSlice = createSlice({
    name: 'searchMedia',
    initialState,
    reducers: {},
    extraReducers: builder => {
        builder.addCase(searchMedia.pending, state => {
            state.loading = true
            state.error = null
            state.payload = null
        }), builder.addCase(searchMedia.rejected, (state, action) => {
            state.loading = false
            state.error = action?.payload ? '' + action?.payload : 'Failed to fetch payload'
            state.payload = null
        }) , builder.addCase(searchMedia.fulfilled, (state, action) => {
            state.loading = false
            state.error = null
            state.payload = action.payload
        })
    }
})

export default mediaSearchResultsSlice