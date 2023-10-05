import { Equalizer, PlayArrow, Refresh } from "@mui/icons-material"
import { Button, Checkbox, FormControl, FormControlLabel, IconButton, InputLabel, List, ListItem, ListItemIcon, ListItemText, MenuItem, Select, TextField } from "@mui/material"
import { ChangeEvent, useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import { NotificationType, addNotification } from "../../../common/notification/notificationSlice"
import { RootState } from "../../../common/redux/store"
import { loadTrack } from "../../music/features/playMusicSlice"
import { PlaylistActionTypePayload, PlaylistTrackPayload, PlaylistWithTracksPayload, getPlaylist } from "../../music/rest/playlistActionCalls"
import { deletePlaylist, updatePlaylist } from "../../playlist/rest/playlistCalls"
import "./MusicPlaylist.css"

const MusicPlaylist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playMusic = useSelector((state: RootState) => state.playMusic)
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

    const handleChangeAction = (action: string): void => {

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
            } else {
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

        deletePlaylist(props.playlistPayload.id, userToken).then(response => {
            if (response.ok) {
                dispatch(
                    addNotification({
                        message: "Playlist deleted",
                        notificationType: NotificationType.SUCCESS
                    })
                )
                navigate("/playlists/all")
            } else {
                dispatch(
                    addNotification({
                        message: "You do not have permission to delete this playlist.",
                        notificationType: NotificationType.ERROR
                    })
                )
            }

        })
    }

    const handleCancel = (): void => {
        navigate("/playlists/all")
    }

    const handleChangePrivate = (e: ChangeEvent<HTMLInputElement>): void => {
        if (!props) {
            return
        }

        setProps({
            ...props,
            playlistPayload: {
                ...props?.playlistPayload,
                privatePlaylist: e.target.checked
            }
        })
    }

    const handleToggleTrack = (e: ChangeEvent<HTMLInputElement>) => {
        if (!props) {
            return
        }

        const index = props?.playlistMediaItemPayloads
            .findIndex(pmi => pmi.playlistMediaItemId === +e.target.value)

        console.log("handleToggleTrack", index)

        if (index < 0) {
            return
        }

        const playlistMediaItemPayloads = props.playlistMediaItemPayloads
        playlistMediaItemPayloads[index].selected = e.target.checked


        setProps({
            ...props,
            playlistMediaItemPayloads
        })

    }

    const handleToggleAllTracks = (e: ChangeEvent<HTMLInputElement>) => {
        if (!props) {
            return
        }

        console.log("handleToggleAllTracks")

        const playlistMediaItemPayloads = props.playlistMediaItemPayloads
        playlistMediaItemPayloads.forEach(pmi => {
            pmi.selected = e.target.checked
        })

        setProps({
            ...props,
            playlistMediaItemPayloads
        })
    }

    const handlePlayTrack = (loadPlaylistMediaItemId: number): void => {
        if (loadPlaylistMediaItemId) {
            dispatch(
                loadTrack({
                    loadPlaylistId: props?.playlistPayload.id,
                    loadPlaylistMediaItemId
                })
            )
        }
    }


    const handlePlayPlaylist = () => {
        console.log("handlePlayPlaylist", props)
        if (!props) {
            return
        }

        dispatch(
            addNotification({
                message: "Loaded playlist.",
                notificationType: NotificationType.SUCCESS
            })
        )

        dispatch(
            loadTrack({
                loadPlaylistId: props.playlistPayload.id,
            })
        )
    }

    const isTrackPlaying = (playlistMediaItemId: number, index: number): boolean => {
        if (!playMusic.loadedPlaylistMediaItemId) {
            return index === 0
        }

        return playlistMediaItemId == playMusic.loadedPlaylistMediaItemId

    }

    const handleReload = () => {
        if (!props?.playlistPayload.id) {
            return
        }

        getPlaylist(props.playlistPayload.id, userToken).then(response => {
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
    }

    return (
        <form id="music-playlist" onSubmit={handleSubmitAction} >

            {playlistId &&
                <h1
                    contentEditable={props?.playlistPayload.edit}
                    suppressContentEditableWarning={true}
                    onBlur={e => handleChangeName(e.target.innerText)}>{props?.playlistPayload.name}
                    <Refresh
                        className="icon-link"
                        onClick={handleReload}
                        color="primary" />
                </h1>

            }

            {!playlistId &&
                <div className="new-line">
                    <TextField
                        name="playlistName"
                        label="Playlist name"
                        value={props?.playlistPayload.name || ""}
                        onChange={e => handleChangeName(e.target.value)}
                        fullWidth={true}
                    />
                </div>
            }

            <List>
                <ListItem
                    secondaryAction={

                        <Button
                            variant="contained"
                            startIcon={<PlayArrow />}
                            onClick={handlePlayPlaylist}>
                            Play
                        </Button>
                    }>
                    <ListItemIcon>
                        <Checkbox
                            edge="start"
                            tabIndex={-1}
                            disableRipple
                            onChange={handleToggleAllTracks}
                        />
                    </ListItemIcon>
                </ListItem>

                {props?.playlistMediaItemPayloads.map(function (track, index) {
                    return (
                        <ListItem
                            key={track.playlistMediaItemId}
                            secondaryAction={
                                isTrackPlaying(track.playlistMediaItemId, index)
                                    ?
                                    <IconButton
                                        edge="end"
                                        aria-label="playing"
                                        onClick={() => handlePlayTrack(track.playlistMediaItemId)}>
                                        <Equalizer
                                            color="primary"
                                        />
                                    </IconButton>
                                    :
                                    <IconButton
                                        edge="end"
                                        aria-label="play"
                                        onClick={() => handlePlayTrack(track.playlistMediaItemId)}>
                                        <PlayArrow
                                            color="primary"
                                        />
                                    </IconButton>
                            }
                        >

                            <ListItemIcon>
                                <Checkbox
                                    edge="start"
                                    tabIndex={-1}
                                    disableRipple
                                    value={track.playlistMediaItemId}
                                    checked={track.selected || false}
                                    onChange={(e) => handleToggleTrack(e)}
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


            {playlistId &&
                <FormControl
                    sx={{
                        width: "10em",
                        marginTop: "1em",
                        marginBottom: "1em"
                    }}>
                    <InputLabel id="playlist-action-label">Action</InputLabel>
                    <Select
                        labelId="playlist-action-label"
                        id="playlist-action"
                        label="Action"
                        value={props?.playlistActionTypePayload || ''}
                        onChange={e => handleChangeAction(e.target.value)}
                    >
                        <MenuItem value=""></MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.REMOVE_ITEMS}>Remove items</MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.MOVE_TOP}>Move to top</MenuItem>
                        <MenuItem value={PlaylistActionTypePayload.MOVE_BOTTOM}>Move to bottom</MenuItem>
                    </Select>
                </FormControl>
            }

            <div className="new-line">
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={props?.playlistPayload.privatePlaylist || false}
                            onChange={handleChangePrivate}
                        />}
                    label="Private playlist" />
            </div>

            <div className="new-line right">
                <Button
                    variant="contained"
                    color="primary"
                    type="submit"
                    sx={{
                        margin: 1,
                        textAlign: "right"
                    }}
                >
                    Save
                </Button>

                {props?.playlistPayload.delete &&
                    <Button
                        variant="contained"
                        color="secondary"
                        type="button"
                        onClick={handleDelete}
                        sx={{ margin: 1 }}
                    >
                        Delete
                    </Button>
                }

                <Button
                    variant="contained"
                    color="secondary"
                    type="button"
                    onClick={handleCancel}
                    sx={{ margin: 1 }}
                >
                    Cancel
                </Button>

            </div>
        </form>
    )
}

export default MusicPlaylist