import { Add, PlayArrow } from "@mui/icons-material"
import { Button, Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import './Album.css'
import { loadTrack } from "./features/playMusicSlice"
import { AlbumWithTracksAndArtistPayload, ImageType, albumArtImageUrl, getAlbum } from "./rest/musicCalls"
import { playAlbum, playTrack } from "./rest/playlistActionCalls"


type AlbumPagePageload = {
    albumWithTracksAndArtistPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    imagePopover: ImagePopoverPayload
}

const Album = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { albumId } = useParams()



    const [props, setProps] = useState<AlbumPagePageload>({
        albumWithTracksAndArtistPayload: ({
            mediaToken: '',
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
        }),
        imagePopover: {
            source: '',
            trigger: 0
        }
    })





    // const [props, setProps] = useState<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>({
    //     mediaToken: "",
    //     payload: {
    //         albumPayload: {
    //             id: 0,
    //             name: ""
    //         },
    //         artistPayload: {
    //             id: 0,
    //             name: ""
    //         },
    //         trackPayloads: []
    //     }
    // })

    useEffect(() => {
        if (albumId) {
            getAlbum(+albumId, userToken).then(response => {
                if (response.parsedBody !== undefined) {

                    setProps(p => ({
                        ...p,
                        imagePopover: {
                            ...p.imagePopover,
                            source: ''
                        },
                        albumWithTracksAndArtistPayload: response.parsedBody

                    }))


                }
            })
        }


    }, [albumId, userToken])

    const albumIdAsNumber = (): number => (
        props.albumWithTracksAndArtistPayload?.payload.albumPayload.id || 0
    )

    const handleImagePopover = () => {

        const albumId = albumIdAsNumber()
        if (!albumId) {
            return
        }

        const source = albumArtImageUrl(
            albumId,
            ImageType.ORIGINAL,
            props.albumWithTracksAndArtistPayload?.mediaToken || '')

        setProps(p => ({
            ...p,
            imagePopover: {
                source,
                trigger: Date.now()
            }
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
                    image={albumArtImageUrl(albumIdAsNumber(), ImageType.ORIGINAL, props.albumWithTracksAndArtistPayload?.mediaToken || '')}
                    height="300"
                    className="cursor-pointer"
                    onClick={handleImagePopover}
                />

                <div className="controls">
                    <Button
                        variant="contained"
                        startIcon={<PlayArrow />}
                        onClick={() => handlePlayAlbum(albumIdAsNumber())}
                        sx={{
                            marginRight: "1em"
                        }}>
                        Play
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => handleAddAlbum(albumIdAsNumber())}>
                        Add
                    </Button>
                </div>
            </div>

            <ImagePopover {...props.imagePopover} />

            <CardContent>
                <div className="album-name">{props.albumWithTracksAndArtistPayload?.payload.albumPayload.name}</div>
                <div className="artist-name">
                    <Link
                        className="link-no-underlne"
                        to={"/music/artist/" + props.albumWithTracksAndArtistPayload?.payload.artistPayload.id}>{props.albumWithTracksAndArtistPayload?.payload.artistPayload.name}
                    </Link>
                </div>

                <List>
                    {props.albumWithTracksAndArtistPayload?.payload.trackPayloads.map(function (trackPayload, index) {
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