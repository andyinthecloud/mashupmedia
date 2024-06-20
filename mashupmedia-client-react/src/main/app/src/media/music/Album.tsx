import { Add, Edit, PlayArrow } from "@mui/icons-material"
import { Button, Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import { useEffect, useRef, useState } from "react"
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
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog"
import ManageMetaImages, { ManageMetaImagesPayload } from "../../common/components/meta/ManageMetaImages"
import { MetaImagePayload } from "./rest/musicUploadCalls"


type AlbumPagePageload = {
    secureMediaItemPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    imagePopover: ImagePopoverPayload
    editAlbumNameDialogPayload: EditTextDialogPayload
    manageMetaImagesPayload: ManageMetaImagesPayload

}

const Album = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const { albumId } = useParams()
    const albumWithTracksAndArtistPayloadRef = useRef<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>()
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)


    const [props, setProps] = useState<AlbumPagePageload>({
        secureMediaItemPayload: ({
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
        },
        editAlbumNameDialogPayload: {
            textFieldLabel: "Name",
            dialogPayload: {
                open: false,
                payload: '',
                title: "Edit album name",
                updatePayload: updateArtistName
            }
        },
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getMetaImageUrl,
            isManager: isManager
        },
    })

    function isManager(): boolean {
        const userPayload = albumWithTracksAndArtistPayloadRef.current?.payload.artistPayload.userPayload
        if (!userPayload || !userPolicyPayload) {
            return false
        }

        return userPolicyPayload.administrator || userPayload.username === userPolicyPayload.username
    }

    function getMetaImageUrl(id: number): string {
        return albumArtImageUrl(
            albumWithTracksAndArtistPayloadRef.current?.payload.albumPayload.id || 0,
            ImageType.ORIGINAL,
            albumWithTracksAndArtistPayloadRef.current?.mediaToken || '',
            id)
    }

    function updateMetaImages(metaImagePayloads: MetaImagePayload[]): void {
        setProps(p => ({
            ...p,
            secureMediaItemPayload: {
                ...p.secureMediaItemPayload,
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    albumPayload: {
                        ...p.secureMediaItemPayload?.payload.albumPayload,
                        id: p.secureMediaItemPayload?.payload.albumPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.albumPayload.name || '',
                        metaImagePayloads

                    },
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || ''
                    },
                    trackPayloads: p.secureMediaItemPayload?.payload.trackPayloads || []
                 }
            }
        }))
    }

    function uploadMetaImages(files: FileList): void {
        console.log('uploadMetaImages', files)
    }

    function updateArtistName(albumName: string): void {
        let name = albumWithTracksAndArtistPayloadRef.current?.payload.albumPayload.name || ''
        if (albumName) {
            name = albumName
        }

        setProps(p => ({
            ...p,
            editAlbumNameDialogPayload: {
                ...p.editAlbumNameDialogPayload,
                dialogPayload: {
                    ...p.editAlbumNameDialogPayload.dialogPayload,
                    open: false
                }
            },
            secureMediaItemPayload: {
                ...p.secureMediaItemPayload,
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    albumPayload: {
                        ...p.secureMediaItemPayload?.payload.albumPayload,
                        id: p.secureMediaItemPayload?.payload.albumPayload.id || 0,
                        name 
                    },
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || ''
                    },
                    trackPayloads: p.secureMediaItemPayload?.payload.trackPayloads || []
                }
            }
        }))
    }


    useEffect(() => {
        if (albumId) {
            getAlbum(+albumId, userToken).then(response => {
                if (response.ok && response.parsedBody) {

                    const secureMediaItemPayload = response.parsedBody

                    setProps(p => ({
                        ...p,
                        imagePopover: {
                            ...p.imagePopover,
                            source: ''
                        },
                        secureMediaItemPayload,
                        manageMetaImagesPayload: {
                            ...p.manageMetaImagesPayload,
                            metaImagePayloads: secureMediaItemPayload.payload.albumPayload.metaImagePayloads || []
                        }
                    }))
                    albumWithTracksAndArtistPayloadRef.current = secureMediaItemPayload
                }
            })
        }


    }, [albumId, userToken])

    const albumIdAsNumber = (): number => (
        props.secureMediaItemPayload?.payload.albumPayload.id || 0
    )

    const handleImagePopover = () => {

        const albumId = albumIdAsNumber()
        if (!albumId) {
            return
        }

        const source = albumArtImageUrl(
            albumId,
            ImageType.ORIGINAL,
            props.secureMediaItemPayload?.mediaToken || '')

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

    function openEditNameDialog(): void {
        setProps(p => ({
            ...p,
            editAlbumNameDialogPayload: {
                ...p.editAlbumNameDialogPayload,
                dialogPayload: {
                    ...p.editAlbumNameDialogPayload.dialogPayload,
                    open: true
                }
            }
        }))
    }

    return (

        <Card id="album">

            <EditTextDialog {...props.editAlbumNameDialogPayload} />

            <div className="media-container">

                <CardMedia
                    component="img"
                    image={albumArtImageUrl(albumIdAsNumber(), ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '')}
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
                <div className="album-name">
                    {props.secureMediaItemPayload?.payload.albumPayload.name}
                    <IconButton
                        color="secondary"
                        onClick={openEditNameDialog}
                    >
                        <Edit />
                    </IconButton>
                </div>
                <div className="artist-name">
                    <Link
                        className="link-no-underlne"
                        to={"/music/artist/" + props.secureMediaItemPayload?.payload.artistPayload.id}>{props.secureMediaItemPayload?.payload.artistPayload.name}
                    </Link>
                </div>

                <ManageMetaImages {...props.manageMetaImagesPayload} />

                <List>
                    {props.secureMediaItemPayload?.payload.trackPayloads.map(function (trackPayload, index) {
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