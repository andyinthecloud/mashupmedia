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
import { AlbumWithTracksAndArtistPayload, ImageType, albumArtImageUrl, deleteAlbum, getAlbum, saveAlbum } from "./rest/musicCalls"
import { playAlbum, playTrack } from "./rest/playlistActionCalls"
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog"
import ManageMetaImages, { ManageMetaImagesPayload } from "../../common/components/meta/ManageMetaImages"
import { MetaImagePayload, uploadAlbumImages } from "./rest/musicUploadCalls"
import { isContentEditor } from "../../common/utils/adminUtils"
import { t } from "i18next"
import ManageExternalLinks, { ManageExternalLinksPayload } from "../../common/components/meta/ManageExternalLinks"
import { ExternalLinkPayload } from "../rest/mediaCalls"


type AlbumPagePageload = {
    secureMediaItemPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    imagePopover: ImagePopoverPayload
    editAlbumNameDialogPayload: EditTextDialogPayload
    editAlbumProfileDialogPayload: EditTextDialogPayload
    manageMetaImagesPayload: ManageMetaImagesPayload
    manageExternalLinksPayload: ManageExternalLinksPayload

}

const Album = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const { albumId } = useParams()
    const albumWithTracksAndArtistPayloadRef = useRef<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>()
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const dispatch = useDispatch()
    const navigate = useNavigate()

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
                updatePayload: updateAlbumName
            }
        },
        editAlbumProfileDialogPayload: {
            textFieldLabel: "Profile",
            dialogPayload: {
                open: false,
                payload: '',
                title: "Edit artist profile",
                updatePayload: updateAlbumProfile
            }
        },
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getMetaImageUrl,
            isManager: isManager
        },
        manageExternalLinksPayload: {
            externalLinkPayloads: [],
            updateExternalLinks: updateExternalLinks,
            isManager: isEditor
        },
    })

    function updateExternalLinks(externalLinkPayloads: ExternalLinkPayload[]): void {
        setProps(p => ({
            ...p,
            secureMediaItemPayload: {
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    albumPayload: {
                        ...p.secureMediaItemPayload?.payload.albumPayload,
                        id: p.secureMediaItemPayload?.payload.albumPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.albumPayload.name || '',
                        externalLinkPayloads
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


    function updateAlbumProfile(profile: string): void {

        setProps(p => ({
            ...p,
            editAlbumProfileDialogPayload: {
                ...p.editAlbumNameDialogPayload,
                dialogPayload: {
                    ...p.editAlbumProfileDialogPayload.dialogPayload,
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
                        name: p.secureMediaItemPayload?.payload.albumPayload.name || '',
                        profile
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
        uploadAlbumImages(albumWithTracksAndArtistPayloadRef.current?.payload.albumPayload.id || 0, files, userToken).then(response => {
            if (response.ok) {
                const metaImagePayloads = albumWithTracksAndArtistPayloadRef.current?.payload.albumPayload.metaImagePayloads || []
                metaImagePayloads.push(...response.parsedBody?.payload || [])
                updateMetaImages(metaImagePayloads)
            } else {
                dispatch(
                    addNotification({
                        message: 'Unable to upload image',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
    }

    function updateAlbumName(name: string): void {

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

    function openEditProfileDialog(): void {
        setProps(p => ({
            ...p,
            editAlbumProfileDialogPayload: {
                ...p.editAlbumNameDialogPayload,
                dialogPayload: {
                    ...p.editAlbumNameDialogPayload.dialogPayload,
                    open: true
                }
            }
        }))
    }

    function isEditor(): boolean {
        return isContentEditor(props.secureMediaItemPayload?.payload.artistPayload.userPayload, userPolicyPayload)
    }

    const handleClickCancel = (): void => {
        navigate('/music/albums')
    }

    const handleClickSave = (): void => {
        const albumPayload = props.secureMediaItemPayload?.payload.albumPayload
        if (!albumPayload) {
            return
        }

        saveAlbum(albumPayload, userToken).then(response => {
            if (response.ok) {
                navigate('/music/albums')
                dispatch(
                    addNotification({
                        message: t('label.albumSaving'),
                        notificationType: NotificationType.SUCCESS
                    })
                )
            } else {
                dispatch(
                    addNotification({
                        message: t(response.parsedBody?.errorPayload.errorCode || 'error.general'),
                        notificationType: NotificationType.ERROR
                    })
                )

            }
        })
    }

    const handleClickDelete = (): void => {
        const albumId = props.secureMediaItemPayload?.payload.albumPayload.id
        if (!albumId) {
            return
        }

        deleteAlbum(albumId, userToken).then(response => {
            if (response.ok) {
                dispatch(
                    addNotification({
                        message: t('label.albumDeleted'),
                        notificationType: NotificationType.SUCCESS
                    })
                )
            } else {
                dispatch(
                    addNotification({
                        message: t(response.parsedBody?.errorPayload.errorCode || 'error.general'),
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })

    }

    return (

        <Card id="album">

            <EditTextDialog {...props.editAlbumNameDialogPayload} />
            <EditTextDialog {...props.editAlbumProfileDialogPayload} />

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
                    <span>
                        {props.secureMediaItemPayload?.payload.albumPayload.name}
                    </span>

                    {isEditor() &&
                        <Button
                            variant="outlined"
                            endIcon={<Edit />}
                            color="secondary"
                            size="small"
                            onClick={openEditNameDialog}
                        >
                            {t('label.name')}
                        </Button>
                    }

                </div>
                <div className="profile">
                    {props.secureMediaItemPayload?.payload.albumPayload.profile &&
                        <span>
                            {props.secureMediaItemPayload?.payload.albumPayload.profile}
                        </span>
                    }

                    {isEditor() &&
                        <Button
                            variant="outlined"
                            endIcon={<Edit />}
                            color="secondary"
                            size="small"
                            onClick={openEditProfileDialog}
                        >
                            {t('label.profile')}
                        </Button>
                    }
                </div>
                <div className="artist-name">
                    <Link
                        className="link-no-underlne"
                        to={"/music/artist/" + props.secureMediaItemPayload?.payload.artistPayload.id}>{props.secureMediaItemPayload?.payload.artistPayload.name}
                    </Link>
                </div>

                <ManageMetaImages {...props.manageMetaImagesPayload} />
                <ManageExternalLinks {...props.manageExternalLinksPayload} />

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


                <div className="new-line right" style={{ marginTop: "1em" }}>
                    <Button
                        variant="contained"
                        color="secondary"
                        type="button"
                        onClick={handleClickCancel}>
                        Cancel
                    </Button>

                    {isEditor() &&
                        <Button
                            variant="contained"
                            color="primary"
                            type="button"
                            onClick={handleClickDelete}
                        >
                            Delete
                        </Button>
                    }

                    {isEditor() &&
                        <Button
                            variant="contained"
                            color="primary"
                            type="button"
                            onClick={handleClickSave}
                        >
                            Save
                        </Button>
                    }

                </div>


            </CardContent>



        </Card>
    )

}

export default Album