import { Add, PlayArrow } from "@mui/icons-material"
import { Button, Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import { t } from "i18next"
import { useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog"
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover"
import MusicMetaMenu, { MusicMetaMenuPagePayload } from "../../common/components/menus/MusicMetaMenu"
import ManageExternalLinks, { ManageExternalLinksPayload } from "../../common/components/meta/ManageExternalLinks"
import ManageMetaImages, { ManageMetaImagesPayload } from "../../common/components/meta/ManageMetaImages"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { isContentEditor } from "../../common/utils/adminUtils"
import { ExternalLinkPayload } from "../rest/mediaCalls"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import './Album.css'
import { loadTrack } from "./features/playMusicSlice"
import { AlbumWithTracksAndArtistPayload, ImageType, albumArtImageUrl, deleteAlbum, getAlbum, saveAlbum } from "./rest/musicCalls"
import { MetaImagePayload, uploadAlbumImages } from "./rest/musicUploadCalls"
import { playAlbum, playTrack } from "./rest/playlistActionCalls"
import CreateAlbumNameDialog, { CreateAlbumNameDialogPageload } from "../../common/components/dialogs/CreateAlbumNameDialog"


type AlbumPagePageload = {
    secureMediaItemPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    imagePopover: ImagePopoverPayload
    editAlbumNameDialogPayload: EditTextDialogPayload
    editAlbumSummaryDialogPayload: EditTextDialogPayload
    manageMetaImagesPayload: ManageMetaImagesPayload
    manageExternalLinksPayload: ManageExternalLinksPayload
    createAlbumDialogPayload: CreateAlbumNameDialogPageload
    musicMetaMenuPagePayload: MusicMetaMenuPagePayload
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
        editAlbumSummaryDialogPayload: {
            textFieldLabel: "Summary",
            dialogPayload: {
                open: false,
                payload: '',
                title: "Edit album summary",
                updatePayload: updateAlbumSummary
            }
        },
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getMetaImageUrl,
            // editor: false
        },
        manageExternalLinksPayload: {
            externalLinkPayloads: [],
            updateExternalLinks: updateExternalLinks,
        },
        createAlbumDialogPayload: {
            artistId: 0
        },
        musicMetaMenuPagePayload: {
            editor: false,
            edit: handleEdit,
            editLabel: t("editAlbum.menuLink"),
            uploadTracks: handleUploadTracks,

            // artistId: 0,
            // editName: openEditNameDialog,
            // editSummary: openEditSummaryDialog,
            // addImage: handleAddImage,
            // addExternalLink: handleAddExternalLink,
            addAlbum: handleAddAlbum
        }
    })

    function handleUploadTracks(): void {
        console.log("handleUploadTracks")
        
    }

    function handleEdit(): void {
        navigate("/music/artist/edit/" + props.secureMediaItemPayload?.payload.artistPayload.id )
    }

    function handleAddAlbum(): void {
        setProps(p => ({
            ...p,
            createAlbumDialogPayload: {
                ...p.createAlbumDialogPayload,
                triggerAddAlbum: Date.now()
            }
        }))
    }

    // function handleAddImage(): void {
    //     setProps(p => ({
    //         ...p,
    //         manageMetaImagesPayload: {
    //             ...p.manageMetaImagesPayload,
    //             triggerUploadImage: Date.now()
    //         }
    //     }))
    // }


    // function handleAddExternalLink(): void {
    //     setProps(p => ({
    //         ...p,
    //         manageExternalLinksPayload: {
    //             ...p.manageExternalLinksPayload,
    //             triggerAddExternalLink: Date.now()
    //         }
    //     }))
    // }

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


    function updateAlbumSummary(summary: string): void {

        setProps(p => ({
            ...p,
            editAlbumSummaryDialogPayload: {
                ...p.editAlbumNameDialogPayload,
                dialogPayload: {
                    ...p.editAlbumSummaryDialogPayload.dialogPayload,
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
                        summary
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
                    albumWithTracksAndArtistPayloadRef.current = secureMediaItemPayload

                    const editor = isEditor()
                    const artistId = secureMediaItemPayload.payload.artistPayload.id

                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload,
                        imagePopover: {
                            ...p.imagePopover,
                            source: ''
                        },
                        manageMetaImagesPayload: {
                            ...p.manageMetaImagesPayload,
                            metaImagePayloads: secureMediaItemPayload.payload.albumPayload.metaImagePayloads || [],
                            editor
                        },
                        createAlbumDialogPayload: {
                            ...p.createAlbumDialogPayload,
                            artistId                        
                        },
                        musicMetaMenuPagePayload: {
                            ...p.musicMetaMenuPagePayload,
                            artistId, 
                            editor
                        }
                    }))

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


    const handleAddAlbumToPLaylist = (albumId: number): void => {
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

    // function openEditNameDialog(): void {
    //     setProps(p => ({
    //         ...p,
    //         editAlbumNameDialogPayload: {
    //             ...p.editAlbumNameDialogPayload,
    //             dialogPayload: {
    //                 ...p.editAlbumNameDialogPayload.dialogPayload,
    //                 open: true
    //             }
    //         }
    //     }))
    // }

    // function openEditSummaryDialog(): void {
    //     setProps(p => ({
    //         ...p,
    //         editAlbumSummaryDialogPayload: {
    //             ...p.editAlbumNameDialogPayload,
    //             dialogPayload: {
    //                 ...p.editAlbumNameDialogPayload.dialogPayload,
    //                 open: true
    //             }
    //         }
    //     }))
    // }

    function isEditor(): boolean {
         return isContentEditor(albumWithTracksAndArtistPayloadRef.current?.payload.artistPayload.userPayload, userPolicyPayload)
    }

    const handleClickCancel = (): void => {
        navigate('/music/artist/' + albumWithTracksAndArtistPayloadRef.current?.payload.artistPayload.id)
    }

    const handleClickSave = (): void => {
        const albumPayload = props.secureMediaItemPayload?.payload.albumPayload
        const artistId = props.secureMediaItemPayload?.payload.artistPayload.id
        if (!albumPayload || !artistId) {
            return
        }

        saveAlbum(
            {
                ...albumPayload,
                artistId
            },
            userToken).then(response => {
                if (response.ok) {
                    navigate('/music/artist/' + artistId)
                    dispatch(
                        addNotification({
                            message: t('label.albumSaved'),
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                } else {
                    dispatch(
                        addNotification({
                            message: t('error.general'),
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
                navigate('/music/artist/' + props.secureMediaItemPayload?.payload.artistPayload.id)
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
            <EditTextDialog {...props.editAlbumSummaryDialogPayload} />

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
                        onClick={() => handleAddAlbumToPLaylist(albumIdAsNumber())}>
                        Add
                    </Button>
                </div>
            </div>

            <ImagePopover {...props.imagePopover} />

            <CardContent>
                <div className="album-name">
                    <div>
                        {props.secureMediaItemPayload?.payload.albumPayload.name}
                    </div>
                    <MusicMetaMenu {...props.musicMetaMenuPagePayload} />
                </div>

                {props.secureMediaItemPayload?.payload.albumPayload.summary &&
                    <div className="summary">
                        {props.secureMediaItemPayload?.payload.albumPayload.summary}
                    </div>
                }

                <div className="artist-name">
                    <Link
                        className="link-no-underlne"
                        to={"/music/artist/" + props.secureMediaItemPayload?.payload.artistPayload.id}>{props.secureMediaItemPayload?.payload.artistPayload.name}
                    </Link>
                </div>

                {isEditor() &&
                    <ManageMetaImages {...props.manageMetaImagesPayload} />
                }

                {isEditor() &&
                    <ManageExternalLinks {...props.manageExternalLinksPayload} />
                }
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

                <CreateAlbumNameDialog {...props.createAlbumDialogPayload} />

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