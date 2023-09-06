import { IconButton, ListItem, ListItemIcon, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { playTrack } from "../music/rest/playlistActionCalls"
import { loadTrack } from "../music/features/playMusicSlice"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { Add, Audiotrack, PlayArrow } from "@mui/icons-material"
import { Link, useNavigate } from "react-router-dom"
import { getTrackYearInBrackets } from "../music/utils/musicItemUtils"
import { MusicSearchResultPayload } from "./features/searchMediaSlice"

const MusicSearchResult = (musicSearchResultPayload: MusicSearchResultPayload) => {

    const [props, setProps] = useState<MusicSearchResultPayload>()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    useEffect(() => {
        setProps(musicSearchResultPayload)

    }, [musicSearchResultPayload])


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

    return (
        <ListItem
            key={props?.trackPayload.id}
            secondaryAction={
                <div>
                    <IconButton
                        edge="end"
                        color="primary"
                        onClick={() => handlePlayItem(props?.trackPayload.id || 0)}>
                        <PlayArrow />
                    </IconButton>
                    <IconButton
                        edge="end"
                        color="primary"
                        onClick={() => handleAddItem(props?.trackPayload.id || 0)}>
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

export default MusicSearchResult