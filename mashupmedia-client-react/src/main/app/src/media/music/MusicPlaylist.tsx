import { Delete, PlayArrow } from "@mui/icons-material"
import { IconButton, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useParams } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { getPlaylistTracks, MusicPlaylistTrackPayload } from "./rest/playlistCalls"

const MusicPlaylist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { playlistId } = useParams()

    const [props, setProps] = useState<MusicPlaylistTrackPayload[]>([])



    useEffect(() => {

        if (!playlistId) {
            return
        }


        getPlaylistTracks(+playlistId, userToken).then(response => {
            if (response.ok) {
                const parsedBody = response.parsedBody
                if (parsedBody) {
                    setProps(parsedBody)
                }
            }
        })


    }, [userToken, playlistId])

    return (
        <div>
            <h1>Music playlist</h1>

            <List>
                {props.map(function (payload, index) {
                    return (
                        <ListItem
                            key={payload.playlistPayload.id}                                  
                            secondaryAction={
                                <IconButton edge="end" aria-label="play">
                                  <Delete />
                                </IconButton>
                              }
                        >

<ListItemIcon>
<IconButton edge="start" aria-label="play">
                                  <PlayArrow />
                                </IconButton>



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
        </div>
    )
}

export default MusicPlaylist