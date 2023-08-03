import { useSearchParams } from "react-router-dom"
import MediaSearchResults from "./MediaSearchResults"
import SearchForm from "./SearchForm"
import { useEffect, useState } from "react"
import { MediaItemSearchCriteriaPayload, MediaSearchResultPayload, searchMedia } from "./rest/searchCalls"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"

type MediaSearchPayload = {
    isShowResults: boolean
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    mediaSearchResultPayloads?: MediaSearchResultPayload[]
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

        callSearchMedia({ searchText })


    }, [userToken, queryParameters])


    const callSearchMedia = (mediaItemSearchCriteriaPayload: MediaItemSearchCriteriaPayload) => {
        searchMedia(mediaItemSearchCriteriaPayload, userToken).then(response => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    mediaItemSearchCriteriaPayload,
                    mediaSearchResultPayloads: response.parsedBody || [],
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
                mediaItemSearchCriteriaPayload={{ searchText: props?.mediaItemSearchCriteriaPayload?.searchText }}
                handleSearchMedia={(mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload) => handleSearchMedia(mediaItemSearchCriteriaPayload)}
            />

            {props.isShowResults &&
                <MediaSearchResults mediaSearchResultPayloads={props?.mediaSearchResultPayloads} />
            }
        </div>
    )
}

export default MediaSearch