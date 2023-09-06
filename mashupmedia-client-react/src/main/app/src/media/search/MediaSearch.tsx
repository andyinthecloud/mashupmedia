import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useSearchParams } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import MediaSearchResults from "./MediaSearchResults"
import SearchForm from "./SearchForm"
import { MediaItemSearchCriteriaPayload, SortType } from "./features/searchMediaSlice"
import { toArray, toInt, toNumberArray } from "../../common/utils/httpUtils"
import { MashupMediaType } from "../music/rest/playlistActionCalls"

const MediaSearch = () => {

    const showResults = useSelector((state: RootState) => state.searchMedia.payload ? true : false)
    const [queryParameters] = useSearchParams()
    const [props, setProps] = useState<MediaItemSearchCriteriaPayload>()


    useEffect(() => {
        if (!queryParameters) {
            return
        }
        
        setProps({
            searchText: queryParameters.get('searchText') || '',
            decades: toNumberArray(queryParameters.get('decades') || ''),
            genreIdNames: toArray(queryParameters.get('genreIdNames') || ''),
            mashupMediaType: MashupMediaType.MUSIC,
            orderBy: queryParameters.get('orderBy') || '',
            pageNumber: toInt(queryParameters.get('pageNumber') || ''),
            sortBy:  Object.values(SortType).find(
                value => value === queryParameters.get('sortBy')
            )            
        })

    }, [queryParameters])



    return (
        <div>
            <h1>Search media</h1>
            <SearchForm
                {...props}
            />

            {showResults &&
                <MediaSearchResults
                />
            }
        </div>
    )
}

export default MediaSearch