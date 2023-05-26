import { HttpMethod, HttpResponse, callMashupMediaApi } from "../../../common/utils/httpUtils"
import { AlbumPayload, ArtistPayload, TrackPayload } from "../../music/rest/musicCalls"
import { MashupMediaType } from "../../music/rest/playlistActionCalls"

const searchUri = "/api/search"
const searchMediaUri = searchUri + "/media"

export type MediaSearchResultPayload = {
    mashupMediaType: MashupMediaType
}

export type MusicSearchResultPayload = MediaSearchResultPayload & {
    trackPayload: TrackPayload
    albumPayload: AlbumPayload 
    artistPayload: ArtistPayload 
}

export const searchMedia = (searchText: string, userToken?: string): Promise<HttpResponse<MediaSearchResultPayload[]>> => {
    return callMashupMediaApi<MediaSearchResultPayload[]>(HttpMethod.GET, `${searchMediaUri}?search=${encodeURIComponent(searchText)}`, userToken)
}