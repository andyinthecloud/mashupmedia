import { Equalizer, PlayArrow } from "@mui/icons-material"
import { Button, Checkbox, FormControl, IconButton, InputLabel, List, ListItem, ListItemIcon, ListItemText, MenuItem, Select } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import { NotificationType, addNotification } from "../../../common/notification/notificationSlice"
import { RootState } from "../../../common/redux/store"
import { deletePlaylist, updatePlaylist } from "../../playlist/rest/playlistCalls"
import "./MusicPlaylist.css"
import { PlaylistActionTypePayload, PlaylistTrackPayload, PlaylistWithTracksPayload, getPlaylist } from "../../music/rest/playlistActionCalls"
import { requestPlaylistTrackId } from "../../music/features/playMusicSlice"


const MusicPlaylist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playlistTrackId = useSelector((state: RootState) => state.playMusic.currentTrackId)
    const { playlistId } = useParams()
    const [props, setProps] = useState<PlaylistWithTracksPayload>()
    const dispatch = useDispatch()

    useEffect(() => {
        if (!playlistId) {
            return
        }

        getPlaylist(+playlistId, userToken).then(response => {
            if (response.ok) {
                const parsedBody = response.parsedBody
                if (parsedBody) {
                    setProps(previous => ({
                        ...previous,
                        mashupMediaType: parsedBody.mashupMediaType,
                        playlistPayload: parsedBody.playlistPayload,
                        playlistMediaItemPayloads: parsedBody.playlistMediaItemPayloads as (PlaylistTrackPayload[])
                    }))
                }
            }
        })
    }, [userToken, playlistId])


    useEffect(() => {
        console.log("playlistTrackId", playlistTrackId)
    }, [playlistTrackId])


    const handleToggleTrack = (playlistMediaItemId: number) => {
        if (!props) {
            return
        }

        const playlistTrackPayload = props?.playlistMediaItemPayloads
            .find(pmi => pmi.playlistMediaItemId === playlistMediaItemId)

        if (!playlistTrackPayload) {
            return
        }

        playlistTrackPayload.selected = playlistTrackPayload.selected ? false : true

    }

    const handleChangeAction = (action: string): void => {

        console.log("handleChangeAction: action", action)

        if (!props) {
            return
        }

        const playlistActionTypePayload = Object.values(PlaylistActionTypePayload).find(
            value => value === action
        )

        setProps({
            ...props,
            playlistActionTypePayload
        })

    }

    const navigate = useNavigate()

    const handleSubmitAction = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        if (!props) {
            return
        }

        updatePlaylist(props, userToken).then(response => {
            if (response.ok) {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Playlist saved.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate("/playlists/all")
                }
            }else {
                response.parsedBody?.errorPayload.objectErrors.map(function (serverError) {
                    dispatch(
                        addNotification({
                            message: serverError.defaultMessage,
                            notificationType: NotificationType.ERROR
                        })
                    )
                })
            }
        })
    }

    const handlePlayTrack = (playlistItemId: number): void => {
        if (playlistItemId) {
            dispatch(
                requestPlaylistTrackId(playlistItemId)
            )
        }
    }

    const handleChangeName = (name: string): void => {
        if (!props) {
            return
        }

        setProps({
            ...props,
            playlistPayload: ({
                ...props.playlistPayload,
                name
            })
        })
    }

    const handleDelete = (): void => {
        
        if (!props?.playlistPayload.id) {
            return
        }

        deletePlaylist(props.playlistPayload.id, userToken).then
    }

    return (
        <div id="music-playlist">
            <h1
                contentEditable={props?.playlistPayload.edit}
                suppressContentEditableWarning={true}
                onBlur={e => handleChangeName(e.target.innerText)}>{props?.playlistPayload.name}</h1>

            <form onSubmit={handleSubmitAction}>

                <FormControl sx={{ width: "10em" }}>
                    <InputLabel id="playlist-action-label">Action</InputLabel>
                    <Select
                        labelId="playlist-action-label"
                        id="playlist-action"
                        label="Action"
                        value={props?.playlistActionTypePayload || ""}
                        onChange={e => handleChangeAction(e.target.value)}
                    >
                        <MenuItem value=""></MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.REMOVE_ITEMS}>Remove items</MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.MOVE_TOP}>Move to top of playlist</MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.MOVE_BOTTOM}>Move to bottom of playlist</MenuItem>
                    </Select>
                </FormControl>

                <Button
                    variant="contained"
                    color="primary"
                    type="submit"
                    sx={{ margin: 1 }}
                >
                    Save
                </Button>

                {props?.playlistPayload.delete &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="button"
                        onClick={handleDelete}
                        sx={{ margin: 1 }}
                    >
                        Delete
                    </Button>
                }

                <List>
                    {props?.playlistMediaItemPayloads.map(function (track, index) {
                        return (
                            <ListItem
                                key={track.playlistMediaItemId}
                                secondaryAction={
                                    playlistTrackId === track.playlistMediaItemId
                                        ?
                                        <IconButton
                                            edge="end"
                                            aria-label="playing"
                                            onClick={() => handlePlayTrack(track.playlistMediaItemId)}>
                                            <Equalizer />
                                        </IconButton>
                                        :
                                        <IconButton
                                            edge="end"
                                            aria-label="play"
                                            onClick={() => handlePlayTrack(track.playlistMediaItemId)}>
                                            <PlayArrow />
                                        </IconButton>
                                }
                            >

                                <ListItemIcon>
                                    <Checkbox
                                        edge="start"
                                        tabIndex={-1}
                                        disableRipple
                                        value={track.playlistMediaItemId}
                                        onChange={(e) => handleToggleTrack(+e.target.value)}
                                    />
                                </ListItemIcon>

                                <ListItemText
                                    primary={`${index + 1} - ${track.trackPayload.name}`}
                                    secondary={<span>
                                        <Link
                                            to={"/music/artist/" + track.artistPayload.id}
                                            className="link-no-underlne"
                                        >
                                            {track.artistPayload.name}
                                        </Link>
                                        <span className="block small">
                                            {track.trackPayload.minutes} min {track.trackPayload.seconds} sec
                                        </span>
                                        {!track.trackPayload.encodedForWeb &&
                                            <span className="block not-encoded">
                                                <span>Incompatible format</span>
                                            </span>
                                        }
                                    </span>
                                    }
                                    sx={{
                                        fontSize: "medium",
                                        fontWeight: "bold"
                                    }}
                                />
                            </ListItem>
                        )
                    })}
                </List>
            </form>
        </div>
    )
}

export default MusicPlaylist