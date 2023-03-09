import { Delete, PlayArrow } from "@mui/icons-material"
import { Button, Checkbox, FormControl, IconButton, InputLabel, List, ListItem, ListItemButton, ListItemIcon, ListItemText, MenuItem, Select } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { useSelector } from "react-redux"
import { useParams } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { getPlaylistTracks, MusicPlaylistTrackPayload, PlaylistActionPayload, PlaylistActionTypePayload, updatePlaylist } from "./rest/playlistCalls"

const MusicPlaylist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { playlistId } = useParams()

    const [props, setProps] = useState<MusicPlaylistTrackPayload[]>([])
    const [playlistActionPayload, setPlaylistActionPayload] = useState<PlaylistActionPayload>({
        playlistId: 0,
        playlistActionTypePayload: PlaylistActionTypePayload.NONE,
        playlistMediaItemIds: []
    })

    useEffect(() => {
        if (!playlistId) {
            return
        }

        setPlaylistActionPayload({
            ...playlistActionPayload,
            playlistId: +playlistId
        })

        getPlaylistTracks(+playlistId, userToken).then(response => {
            if (response.ok) {
                const parsedBody = response.parsedBody
                if (parsedBody) {
                    setProps(parsedBody)
                }
            }
        })


    }, [userToken, playlistId])

    const handleToggleTrack = (playlistMediaItemId: number) => {
        const index = playlistActionPayload.playlistMediaItemIds.indexOf(playlistMediaItemId)
        let playlistMediaItemIds = playlistActionPayload.playlistMediaItemIds
        if (index >= 0) {
            playlistMediaItemIds = playlistActionPayload.playlistMediaItemIds.splice(index)
        } else {
            playlistMediaItemIds.push(playlistMediaItemId)
        }

        setPlaylistActionPayload({
            ...playlistActionPayload,
            playlistMediaItemIds
        })

    }

    const handleChangeAction = (action: string): void => {

        console.log("handleChangeAction: action", action)

        const playlistActionTypePayload = Object.values(PlaylistActionTypePayload).find(
            value => value === action
        );

        setPlaylistActionPayload({
            ...playlistActionPayload,
            playlistActionTypePayload: playlistActionTypePayload || PlaylistActionTypePayload.NONE
        })

    }

    const handleSubmitAction = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        console.log("handleSubmitAction", playlistActionPayload)

        if (!playlistActionPayload.playlistActionTypePayload || playlistActionPayload.playlistActionTypePayload === PlaylistActionTypePayload.NONE ) {
            return
        }

        if (playlistActionPayload.playlistMediaItemIds.length === 0) {
            return
        }

        updatePlaylist(playlistActionPayload, userToken).then(response => {
            if (response.ok) {
                const parsedBody = response.parsedBody
                if (parsedBody) {
                    setProps(parsedBody)
                }
            }
        })
    }

    return (
        <div>
            <h1>Music playlist</h1>

            <form onSubmit={handleSubmitAction}>

                <FormControl sx={{ width: "10em" }}>
                    <InputLabel id="playlist-action-label">Action</InputLabel>
                    <Select
                        labelId="playlist-action-label"
                        id="playlist-action"
                        label="Action"
                        value={playlistActionPayload.playlistActionTypePayload}
                        onChange={e => handleChangeAction(e.target.value)}
                    >
                        <MenuItem value={PlaylistActionTypePayload.NONE}>Select action</MenuItem>
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
                    OK
                </Button>

                <List>
                    {props.map(function (payload, index) {
                        return (
                            <ListItem
                                key={payload.id}
                                secondaryAction={
                                    <IconButton edge="end" aria-label="play">
                                        <PlayArrow />
                                    </IconButton>
                                }
                            >

                                <ListItemIcon>
                                    <Checkbox
                                        edge="start"
                                        tabIndex={-1}
                                        disableRipple
                                        value={payload.id}
                                        onChange={(e) => handleToggleTrack(+e.target.value)}
                                    />
                                </ListItemIcon>

                                <ListItemText
                                    primary={`${index + 1} - ${payload.trackPayload.name}`}
                                    secondary={payload.artistPayload.name}
                                    sx={{
                                        fontSize: "medium"
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