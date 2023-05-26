import { Button, List, ListItem, ListItemButton, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { MashupMediaType, PlaylistPayload } from "../music/rest/playlistActionCalls"
import { getPlaylists } from "./rest/playlistCalls"

type PlaylistsPayload = {
    playlistPayloads: PlaylistPayload[]
}

const Playlists = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [props, setProps] = useState<PlaylistsPayload>()

    useEffect(() => {



        console.log("userEffect: userToken", userToken)
        getPlaylists(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps({ playlistPayloads: response.parsedBody })
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

    function handleNewPlaylist(): void {
        navigate('/playlists/music')
    }

    return (
        <form>
            <h1>Playlists</h1>

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
                <Button variant="contained" color="primary" type="button" onClick={handleNewPlaylist}>
                    New
                </Button>
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>
            </div>

        </form>
    )
}

export default Playlists