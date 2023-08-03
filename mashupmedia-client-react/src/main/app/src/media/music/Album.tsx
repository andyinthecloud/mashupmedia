import { Add, PlayArrow } from "@mui/icons-material"
import { Button, Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import React, { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover"
import { addNotification, NotificationType } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { loadTrack } from "./features/playMusicSlice"
import { albumArtImageUrl, AlbumWithTracksAndArtistPayload, getAlbum, ImageType } from "./rest/musicCalls"
import { addAlbum, addTrack, playAlbum, playTrack } from "./rest/playlistActionCalls"
import './Album.css';

const Album = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { albumId } = useParams()

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>({
        mediaToken: "",
        payload: {
            albumPayload: {
                id: 0,
                name: ""
            },
            artistPayload: {
                id: 0,
                name: ""
            },
            trackPayloads: []
        }
    })

    useEffect(() => {
        if (albumId) {
            getAlbum(+albumId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)

                    setImagePopoverPayload({
                        imageSource: albumArtImageUrl(
                            response.parsedBody.payload.albumPayload.id,
                            ImageType.ORIGINAL,
                            response.parsedBody.mediaToken),
                        timestamp: Date.now()
                    })
                }
            })
        }

    }, [albumId, userToken])

    const albumIdAsNumber = (): number => {
        if (props) {
            return props.payload.albumPayload.id;
        } else {
            return 0
        }
    }

    const [imagePopoverPayload, setImagePopoverPayload] = useState<ImagePopoverPayload>()

    const handleImagePopover = (event: React.MouseEvent<HTMLElement>) => {
        setImagePopoverPayload(p => ({
            ...p,
            anchorELement: event.currentTarget,
            timestamp: Date.now()
        }))
    }


    const dispatch = useDispatch()

    const handlePlayAlbum = (albumId: number): void => {
        playAlbum(albumId, userToken).then((response) => {
            if (response.ok) {
                dispatch(
                    loadTrack({})
                )
                addNotification({
                    message: "Replaced playlist",
                    notificationType: NotificationType.SUCCESS
                })
            }
        })
    }

    const navigate = useNavigate()

    const handleAddAlbum = (albumId: number): void => {
        navigate("/playlists/music/select?albumId=" + albumId)
    }

    const handlePlayTrack = (trackId: number): void => {
        playTrack(trackId, userToken).then((response) => {
            if (response.ok) {
                dispatch(
                    loadTrack({})
                )
                addNotification({
                    message: "Replaced playlist",
                    notificationType: NotificationType.SUCCESS
                })
            }
        })
    }

    const handleAddTrack = (trackId: number): void => {
        navigate("/playlists/music/select?trackId=" + trackId)
    }


    return (

        <Card id="album">

            <div className="media-container">

                <CardMedia
                    component="img"
                    image={albumArtImageUrl(albumIdAsNumber(), ImageType.ORIGINAL, props?.mediaToken)}
                    height="300"
                    className="cursor-pointer"
                    onClick={handleImagePopover}
                />

                <div className="controls">
                    <Button
                        variant="contained"
                        startIcon={<PlayArrow />}
                        onClick={() => handlePlayAlbum(props.payload.albumPayload.id)}
                        sx={{
                            marginRight: "1em"
                        }}>
                        Play
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => handleAddAlbum(props.payload.albumPayload.id)}>
                        Add
                    </Button>
                </div>
            </div>

            <ImagePopover {...imagePopoverPayload} />

            <CardContent>
                <div className="album-name">{props.payload.albumPayload.name}</div>
                <div className="artist-name">
                    <Link
                        className="link-no-underlne"
                        to={"/music/artist/" + props.payload.artistPayload.id}>{props.payload.artistPayload.name}
                    </Link>
                </div>

                <List>
                    {props.payload.trackPayloads.map(function (trackPayload, index) {
                        return (
                            <ListItem
                                className={trackPayload.encodedForWeb ? "" : "track-not-encoded-for-web"}
                                secondaryAction={
                                    <div>
                                        <IconButton
                                            edge="end"
                                            color="primary"
                                            onClick={() => handlePlayTrack(trackPayload.id)}>
                                            <PlayArrow />
                                        </IconButton>
                                        <IconButton
                                            edge="end"
                                            color="primary"
                                            onClick={() => handleAddTrack(trackPayload.id)}>
                                            <Add />
                                        </IconButton>
                                    </div>
                                }

                                key={trackPayload.id}>

                                <ListItemText
                                    primary={`${index + 1} - ${trackPayload.name}`}
                                    secondary={`${trackPayload.minutes} min ${trackPayload.seconds} sec`}
                                />
                            </ListItem>
                        )
                    })}
                </List>
            </CardContent>



        </Card>
    )

}

export default Album