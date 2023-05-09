import { Button, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Radio, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate, useSearchParams } from "react-router-dom"
import { RootState } from "../../../common/redux/store"
import { EncoderStatusType, PlaylistPayload, addAlbum, addArtist, addTrack } from "../../music/rest/playlistActionCalls"
import { getPlaylists } from "../rest/playlistCalls"
import { ServerResponsePayload, getFirstObjectErrorDefaultMessage } from "../../../common/utils/formValidationUtils"
import { NotificationType, addNotification } from "../../../common/notification/notificationSlice"
import { HttpResponse } from "../../../common/utils/httpUtils"
import { playlistNotification } from "../../music/rest/playlistActionUtils"
import { useDispatch } from "react-redux"

type SelectMusicPlaylistPayload = {
    playlistPayloads: PlaylistPayload[]
    playlistId?: number
    createPlaylistName?: string
    trackId?: number
    albumId?: number
    artistId?: number
}

const SelectMusicPlaylist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [props, setProps] = useState<SelectMusicPlaylistPayload>()
    const [queryParameters] = useSearchParams()

    useEffect(() => {
        getPlaylists(userToken).then(response => {
            if (response.ok && response.parsedBody) {
                const playlistPayloads = response.parsedBody
                    .filter((playlist: PlaylistPayload) => playlist.edit)
                setProps({
                    playlistPayloads,
                    trackId: getNumberValue("trackId", queryParameters),
                    albumId: getNumberValue("albumId", queryParameters),
                    artistId: getNumberValue("artistId", queryParameters)
                })
            }
        })
    }, [userToken])

    const getNumberValue = (name: string, queryParameters: URLSearchParams): number | undefined => {
        const value = queryParameters.get(name)
        return value ? +value : undefined
    }

    const handleClickPlaylist = (playlistId: number): void => {
        const playlistPayloads: PlaylistPayload[] = []
        if (props?.playlistPayloads) {
            props.playlistPayloads.forEach(playlistPayload => {
                playlistPayload.selected = playlistPayload.id === playlistId
                playlistPayloads.push(playlistPayload)
            })
        }

        setProps(previous => ({
            ...previous,
            playlistPayloads,
            playlistId
        }))
    }

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate(-1)
    }

    const handleChangeNewPlaylistName = (name: string): void => (
        setProps(previous => ({
            ...previous,
            playlistPayloads: previous?.playlistPayloads || [],
            createPlaylistName: name
        }))
    )

    const dispatch = useDispatch()

    const handleSelectPlaylist = () => {
        if (props?.trackId) {
            addTrack({
                createPlaylistName: props.createPlaylistName,
                playlistId: props.playlistId,
                trackId: props.trackId
            }, userToken).then(processAddPlaylistResponse)
        } else if (props?.albumId) {
            addAlbum({
                createPlaylistName: props.createPlaylistName,
                playlistId: props.playlistId,
                albumId: props.albumId
            }, userToken).then(processAddPlaylistResponse)
        } else if (props?.artistId) {
            addArtist({
                createPlaylistName: props.createPlaylistName,
                playlistId: props.playlistId,
                artistId: props.artistId
            }, userToken).then(processAddPlaylistResponse)
        }
    }

    const processAddPlaylistResponse = (response: HttpResponse<ServerResponsePayload<EncoderStatusType>>): void => {
        if (response.ok) {
            dispatch(
                playlistNotification(response.parsedBody?.payload)
            )
            navigate(-1)
        } else {
            const defaultMessage = getFirstObjectErrorDefaultMessage(response.parsedBody?.errorPayload.objectErrors)
            dispatch(
                addNotification({
                    message: defaultMessage,
                    notificationType: NotificationType.ERROR
                })
            )
        }
    }

    return (
        <form>
            <h1>Select playlist</h1>

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
                <Button variant="contained" color="primary" type="button" onClick={handleSelectPlaylist}>
                    Create
                </Button>
            </div>

            <List>
                {props?.playlistPayloads.map(function (playlist, index) {
                    return (
                        <ListItem key={playlist.id} onClick={() => handleClickPlaylist(playlist.id)}>
                            <ListItemButton>
                                <ListItemIcon>
                                    <Radio
                                        edge="start"
                                        checked={props.playlistPayloads[index].selected || false}
                                        tabIndex={-1}
                                        disableRipple
                                    />
                                </ListItemIcon>
                                <ListItemText>{playlist.name}</ListItemText>
                            </ListItemButton>
                        </ListItem>
                    )
                })}
            </List>

            <div className="new-line right">
                <Button variant="contained" color="primary" type="button" onClick={handleSelectPlaylist}>
                    Select
                </Button>
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>
            </div>

        </form>
    )
}

export default SelectMusicPlaylist