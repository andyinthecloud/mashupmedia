import { useSearchParams } from "react-router-dom"
import MusicSearchResults from "./MusicSearchResults"
import SearchForm from "./SearchForm"
import { useEffect, useState } from "react"
import { MediaItemSearchCriteriaPayload, MediaSearchResultPayload, MusicSearchResultPayload, searchMedia } from "./rest/searchCalls"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { MashupMediaType } from "../music/rest/playlistActionCalls"
import { PagePayload } from "../../common/payload/container"

type MediaSearchPayload = {
    isShowResults: boolean
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    pagePayload?: PagePayload<MediaSearchResultPayload>
}

const MediaSearch = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [queryParameters] = useSearchParams()
    const [props, setProps] = useState<MediaSearchPayload>({
        isShowResults: false
    })

    useEffect(() => {

        const searchText = queryParameters.get("search")
        if (!searchText) {
            return
        }

        callSearchMedia({ searchText, mashupMediaType: MashupMediaType.MUSIC })

    }, [userToken, queryParameters])


    const callSearchMedia = (mediaItemSearchCriteriaPayload: MediaItemSearchCriteriaPayload) => {
        searchMedia(mediaItemSearchCriteriaPayload, userToken).then(response => {
            if (response.ok) {

                console.log('callSearchMedia', response.parsedBody)

                setProps(p => ({
                    ...p,
                    mediaItemSearchCriteriaPayload,
                    pagePayload: response.parsedBody,
                    isShowResults: true
                })
                )
            }
        })
    }


    const handleSearchMedia = (mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload) => {
        if (!mediaItemSearchCriteriaPayload) {
            return
        }
        callSearchMedia(mediaItemSearchCriteriaPayload)
    }


    return (
        <div>
            <h1>Search media</h1>
            <SearchForm
                mediaItemSearchCriteriaPayload={props?.mediaItemSearchCriteriaPayload}
                handleSearchMedia={(mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload) => handleSearchMedia(mediaItemSearchCriteriaPayload)}
            />

            {props.isShowResults &&
                <MusicSearchResults pagePayload={props.pagePayload} />
            }
        </div>
    )
}

export default MediaSearch