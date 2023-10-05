import { List, Pagination } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useAppDispatch } from "../../common/redux/hooks"
import { RootState } from "../../common/redux/store"
import { MashupMediaType } from "../music/rest/playlistActionCalls"
import "./MediaSearchResults.css"
import MusicSearchResult from "./MusicSearchResult"
import { MediaItemSearchCriteriaPayload, MediaSearchResultsPayload, MusicSearchResultPayload, searchMedia } from "./features/searchMediaSlice"


const MediaSearchResults = () => {

    const mediaSearchResultsPage = useSelector((state : RootState) => state.searchMedia.payload)
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [props, setProps] = useState<MediaSearchResultsPayload>()

    useEffect(() => {
        setProps(mediaSearchResultsPage || undefined)
    }, [mediaSearchResultsPage])

    const dispatch = useAppDispatch()
    const handleChangePagination = (pageNumber: number): void => {

        const paginatedCriteria: MediaItemSearchCriteriaPayload = ({
            ...props?.mediaItemSearchCriteriaPayload,
            pageNumber: pageNumber - 1,
            totalElements: props?.pagePayload?.totalElements
        })

        dispatch(
            searchMedia({
                mediaItemSearchCriteriaPayload: paginatedCriteria,
                token: userToken || ''
            })
        )
    }

    const hasSearchResults = (): boolean => {
        return props?.pagePayload?.totalElements ? true : false
    }

    const page = (pageNumber?: number): number => {
        return pageNumber ? pageNumber + 1 : 1
    }

    return (
        <div id="music-search-results">
            <h2>Results</h2>

            {hasSearchResults() &&
                <div>
                    <Pagination
                        className="pagination"
                        page={page(props?.pagePayload?.pageNumber)}
                        count={props?.pagePayload?.totalPages}
                        color="secondary"
                        onChange={(_e, page) => handleChangePagination(page)}                        
                    />
                    <List>
                        {props?.pagePayload?.content?.map(function (mediaSearchResultPayload, index) {

                            if (mediaSearchResultPayload.mashupMediaType === MashupMediaType.MUSIC) {
                                const musicSearchResultPayload = mediaSearchResultPayload as MusicSearchResultPayload
                                return (
                                    <MusicSearchResult
                                        key={musicSearchResultPayload.trackPayload.id}
                                        {...musicSearchResultPayload} />
                                )
                            }
                        })}
                    </List>
                </div>
            }

            {!hasSearchResults() &&
                <p>Unable to find anything matching your search</p>
            }

        </div>
    )
}

export default MediaSearchResults