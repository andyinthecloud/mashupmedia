import { Add, Audiotrack, PlayArrow } from "@mui/icons-material"
import { IconButton, List, ListItem, ListItemIcon, ListItemText, Pagination } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate } from "react-router-dom"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { PagePayload } from "../../common/payload/container"
import { RootState } from "../../common/redux/store"
import { loadTrack } from "../music/features/playMusicSlice"
import { playTrack } from "../music/rest/playlistActionCalls"
import { getTrackYearInBrackets } from "../music/utils/musicItemUtils"
import "./MusicSearchResults.css"
import { MediaItemSearchCriteriaPayload, MediaSearchResultPayload, MusicSearchResultPayload } from "./rest/searchCalls"


export type MusicSearchResultsPayload = {
    pagePayload?: PagePayload<MediaSearchResultPayload>
    // handlePaginate: (page: number) => void

}

const MusicSearchResults = (musicSearchResultsPayload: MusicSearchResultsPayload) => {

    const [props, setProps] = useState<MusicSearchResultsPayload>()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    useEffect(() => {
        setProps({
            pagePayload: musicSearchResultsPayload.pagePayload
        })

    }, [musicSearchResultsPayload.pagePayload])

    const dispatch = useDispatch()
    const handlePlayItem = (mediaItemId: number): void => {

        playTrack(mediaItemId, userToken).then((response) => {
            if (response.ok) {
                dispatch(
                    loadTrack({})
                )
                dispatch(
                    addNotification({
                        message: "Replaced playlist",
                        notificationType: NotificationType.SUCCESS
                    })
                )
            }
        })
    }

    const navigate = useNavigate()
    const handleAddItem = (mediaItemId: number): void => {
        navigate("/playlists/music/select?trackId=" + mediaItemId)
    }


    const displaySearchResult = (mediaSearchResultPayload: MediaSearchResultPayload, index: number) => {

        const musicSearchResultPayload = mediaSearchResultPayload as MusicSearchResultPayload
        const playMediaItemId = musicSearchResultPayload.trackPayload.id

        return (
            <ListItem
                key={index}
                secondaryAction={
                    <div>
                        <IconButton
                            edge="end"
                            color="primary"
                            onClick={() => handlePlayItem(playMediaItemId)}>
                            <PlayArrow />
                        </IconButton>
                        <IconButton
                            edge="end"
                            color="primary"
                            onClick={() => handleAddItem(playMediaItemId)}>
                            <Add />
                        </IconButton>
                    </div>
                }
            >
                <ListItemIcon>
                    <Audiotrack />
                </ListItemIcon>

                <ListItemText
                    primary={`${musicSearchResultPayload.trackPayload.name} ${getTrackYearInBrackets(musicSearchResultPayload.trackPayload.year)}`}
                    secondary={<span>
                        <Link
                            to={"/music/album/" + musicSearchResultPayload.albumPayload.id}
                            className="link-no-underlne album"
                        >{musicSearchResultPayload.albumPayload.name}</Link>
                        <br />
                        <Link
                            to={"/music/artist/" + musicSearchResultPayload.artistPayload.id}
                            className="link-no-underlne artist"
                        >{musicSearchResultPayload.artistPayload.name}</Link>

                    </span>}
                />
            </ListItem>
        )
    }


    const handleChangePagination = (page: number): void => {
        console.log('handleChangePagination: page = ', page)


    }

    const hasSearchResults = (): boolean => {
        return props?.pagePayload?.content?.length ? true : false
    }

    return (
        <div id="music-search-results">
            <h2>Results</h2>

            {hasSearchResults() &&
                <div>
                    <Pagination
                        className="pagination"
                        page={props?.pagePayload?.pageNumber}
                        count={props?.pagePayload?.totalPages}
                        color="secondary"
                        onChange={(_e, page) => handleChangePagination(page)}
                    />
                    <List>
                        {props?.pagePayload?.content?.map(function (mediaSearchResultPayload, index) {
                            return (
                                displaySearchResult(mediaSearchResultPayload, index)
                            )
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

export default MusicSearchResults