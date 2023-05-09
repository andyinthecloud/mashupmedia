import { Button, List, ListItem, ListItemButton, ListItemText, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { MashupMediaType, PlaylistPayload } from "../music/rest/playlistActionCalls"
import { getPlaylists } from "./rest/playlistCalls"

type PlaylistsPayload = {
    playlistPayloads: PlaylistPayload[]
    createPlaylistName?: string
}

const Playlists = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [props, setProps] = useState<PlaylistsPayload>()

    useEffect(() => {
        getPlaylists(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps({playlistPayloads: response.parsedBody})
            }
        })

    }, [userToken])

    const handleClickPlaylist = (playlistId: number, mashupMediaType: MashupMediaType): void => {
        switch (mashupMediaType) {
            case MashupMediaType.MUSIC:
                navigate("/playlists/music/" + playlistId)
                break;        
            default:
                break;
        }
    }

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }

    const handleCreatePlaylist = () => {
        console.log("handleCreatePlaylist")
    }

    const handleChangeNewPlaylistName = (name: string) => {
        console.log("handleCreatePlaylist")
    }

    return (
        <form>
            <h1>Playlists</h1>

            <div className="new-line">
                <TextField
                    name="createPlaylistName"
                    label="New playlist"
                    value={props?.createPlaylistName}
                    onChange={e => handleChangeNewPlaylistName(e.target.value)}
                    fullWidth={true}
                    helperText="Choose a name for your new playlist"
                />
            </div>
            <div className="new-line right">
                <Button variant="contained" color="primary" type="button" onClick={handleCreatePlaylist}>
                    Create
                </Button>
            </div>

            <List>
                {props?.playlistPayloads.map(function (playlist) {
                    return (
                        <ListItem key={playlist.id} onClick={() => handleClickPlaylist(playlist.id, playlist.mashupMediaType)}>
                            <ListItemButton>
                                <ListItemText>{playlist.name}</ListItemText>
                            </ListItemButton>
                        </ListItem>
                    )
                })}
            </List>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>
            </div>

        </form>
    )
}

export default Playlists