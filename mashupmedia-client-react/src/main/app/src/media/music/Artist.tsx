import { Edit } from "@mui/icons-material";
import { Button, Grid } from '@mui/material';
import { t } from "i18next";
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover";
import CreateAlbumNameDialog, { CreateAlbumNameDialogPageload } from "../../common/components/dialogs/CreateAlbumNameDialog";
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog";
import AlbumSummary from '../../common/components/media/AlbumSummary';
import ManageExternalLinks, { ManageExternalLinksPayload } from "../../common/components/meta/ManageExternalLinks";
import ManageMetaImages, { ManageMetaImagesPayload } from "../../common/components/meta/ManageMetaImages";
import { NotificationType, addNotification } from "../../common/notification/notificationSlice";
import { RootState } from '../../common/redux/store';
import { isContentEditor } from "../../common/utils/adminUtils";
import { ExternalLinkPayload } from "../rest/mediaCalls";
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import './Artist.css';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, ImageType, artistImageUrl, deleteArtist, getArtist, saveArtist } from './rest/musicCalls';
import { MetaImagePayload, uploadArtistImages } from "./rest/musicUploadCalls";

type ArtistPagePayload = {
    secureMediaItemPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    editArtistNameDialogPayload: EditTextDialogPayload
    editArtistProfileDialogPayload: EditTextDialogPayload
    manageMetaImagesPayload: ManageMetaImagesPayload
    manageExternalLinksPayload: ManageExternalLinksPayload
    artistImagePopover: ImagePopoverPayload
    createAlbumDialogPayload: CreateAlbumNameDialogPageload
}

const Artist = () => {
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const { artistId } = useParams()
    const artistPayloadRef = useRef<SecureMediaPayload<ArtistWithAlbumsPayload>>()

    const [props, setProps] = useState<ArtistPagePayload>({
        secureMediaItemPayload: {
            mediaToken: '',
            payload: {
                artistPayload: {
                    id: 0,
                    name: '',
                    externalLinkPayloads: []
                },
                albumPayloads: []
            }
        },
        editArtistNameDialogPayload: {
            textFieldLabel: "Name",
            dialogPayload: {
                open: false,
                payload: '',
                title: "Edit artist name",
                updatePayload: updateArtistName
            }
        },
        editArtistProfileDialogPayload: {
            textFieldLabel: "Profile",
            dialogPayload: {
                open: false,
                payload: '',
                title: "Edit artist profile",
                updatePayload: updateArtistProfile
            }
        },
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getMetaImageUrl,
            isManager: showManageButtons
        },
        manageExternalLinksPayload: {
            externalLinkPayloads: [],
            updateExternalLinks: updateExternalLinks,
            isManager: showManageButtons
        },
        artistImagePopover: {
            source: '',
            trigger: 0
        },
        createAlbumDialogPayload: {
            artistId: 0
        }
    })

    function getMetaImageUrl(id: number): string {
        return artistImageUrl(
            artistPayloadRef.current?.payload.artistPayload.id || 0,
            ImageType.ORIGINAL,
            artistPayloadRef.current?.mediaToken
            || '',
            id)
    }

    function uploadMetaImages(files: FileList): void {
        uploadArtistImages(artistPayloadRef.current?.payload.artistPayload.id || 0, files, userToken)
            .then(response => {
                if (response.ok) {
                    const metaImagePayloads = artistPayloadRef.current?.payload.artistPayload.metaImagePayloads || []
                    metaImagePayloads.push(...response.parsedBody?.payload || [])

                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload: {
                            mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                            payload: {
                                albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                                artistPayload: {
                                    ...p.secureMediaItemPayload?.payload.artistPayload,
                                    id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                                    name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                                    metaImagePayloads
                                }
                            }
                        },
                        manageMetaImagesPayload: {
                            ...p.manageMetaImagesPayload,
                            metaImagePayloads
                        }
                    }))
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

    function updateMetaImages(metaImagePayloads: MetaImagePayload[]): void {
        setProps(p => ({
            ...p,
            secureMediaItemPayload: {
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        metaImagePayloads
                    }
                }
            }
        }))
    }

    function updateExternalLinks(externalLinkPayloads: ExternalLinkPayload[]): void {
        setProps(p => ({
            ...p,
            secureMediaItemPayload: {
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        externalLinkPayloads
                    }
                }
            }
        }))
    }


    function updateArtistName(text: string): void {
        setProps(p => ({
            ...p,
            editArtistNameDialogPayload: {
                ...p.editArtistNameDialogPayload,
                dialogPayload: {
                    ...p.editArtistNameDialogPayload.dialogPayload,
                    open: false
                }
            },
            secureMediaItemPayload: {
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || text
                    }
                }
            }
        }))
    }

    function updateArtistProfile(text: string): void {
        setProps(p => ({
            ...p,
            editArtistProfileDialogPayload: {
                ...p.editArtistProfileDialogPayload,
                dialogPayload: {
                    ...p.editArtistProfileDialogPayload.dialogPayload,
                    open: false
                }
            },
            secureMediaItemPayload: {
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        profile: text,

                    }
                }
            }
        }))
    }

    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    const artistWithAlbumsPayload = response.parsedBody

                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload: artistWithAlbumsPayload,
                        manageMetaImagesPayload: {
                            ...p.manageMetaImagesPayload,
                            metaImagePayloads: artistWithAlbumsPayload.payload.artistPayload.metaImagePayloads || []
                        },
                        manageExternalLinksPayload: {
                            ...p.manageExternalLinksPayload,
                            externalLinkPayloads: artistWithAlbumsPayload.payload.artistPayload.externalLinkPayloads || []
                        },
                        createAlbumDialogPayload: {
                            ...p.createAlbumDialogPayload,
                            artistId: artistWithAlbumsPayload.payload.artistPayload.id
                        }
                    }))

                    artistPayloadRef.current = response.parsedBody
                }
            })
        }
    }, [artistId])

    const openEditNameDialog = () => {
        setProps(p => ({
            ...p,
            editArtistNameDialogPayload: {
                ...p.editArtistNameDialogPayload,
                dialogPayload: {
                    ...p.editArtistNameDialogPayload.dialogPayload,
                    open: true
                }
            }
        }))
    }

    const openEditProfileDialog = () => {
        setProps(p => ({
            ...p,
            editArtistProfileDialogPayload: {
                ...p.editArtistNameDialogPayload,
                dialogPayload: {
                    ...p.editArtistNameDialogPayload.dialogPayload,
                    open: true
                }
            }
        }))
    }

    const dispatch = useDispatch()
    const navigate = useNavigate()

    const handleClickDelete = (): void => {
        const artistId = props.secureMediaItemPayload?.payload.artistPayload.id
        if (!artistId) {
            return
        }

        deleteArtist(artistId).then(response => {
            console.log('deleteArtist', response)

            if (response.ok) {
                dispatch(
                    addNotification({
                        message: 'Deleted artist.',
                        notificationType: NotificationType.SUCCESS
                    })
                )
                navigate('/music/artists')
            } else {

                dispatch(
                    addNotification({
                        message: response.parsedBody?.value || '',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
    }


    const handleClickCancel = (): void => {
        navigate('/music/artists')
    }

    const handleClickSave = (): void => {
        const artistPayload = props.secureMediaItemPayload?.payload.artistPayload
        if (!artistPayload) {
            return
        }

        saveArtist(artistPayload).then(response => {
            if (response.ok) {
                if (artistPayload) {
                    dispatch(
                        addNotification({
                            message: 'Saved artist.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/music/artist/' + artistPayloadRef.current?.payload.artistPayload.id)
                } else {
                    dispatch(
                        addNotification({
                            message: 'Please choose a unique name.',
                            notificationType: NotificationType.ERROR
                        })
                    )
                }
            } else {
                dispatch(
                    addNotification({
                        message: 'Error saving artist.',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
    }

    function showManageButtons(): boolean {
        return isContentEditor(artistPayloadRef.current?.payload.artistPayload.userPayload, userPolicyPayload)
    }


    function handleArtistImagePopover() {
        const source = artistImageUrl(
            props.secureMediaItemPayload?.payload.artistPayload.id || 0,
            ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
            props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads?.length ? props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads[0].id : 0
        )

        setProps(p => ({
            ...p,
            artistImagePopover: {
                source,
                trigger: Date.now()
            }
        }))
    }

    return (
        <form id='artist'>

            <EditTextDialog {...props.editArtistNameDialogPayload} />
            <EditTextDialog {...props.editArtistProfileDialogPayload} />
            <ImagePopover {...props.artistImagePopover} />

            <div className="title">

                <img
                    src={artistImageUrl(
                        props.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
                        props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads?.length ? props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads[0].id : 0
                    )}
                    onClick={handleArtistImagePopover}
                />

                <h1>{props.secureMediaItemPayload?.payload.artistPayload.name}</h1>

                {showManageButtons() &&
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

                {props.secureMediaItemPayload?.payload.artistPayload.profile &&
                <div>{props.secureMediaItemPayload?.payload.artistPayload.profile}</div>
}
                {showManageButtons() &&
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

            <ManageMetaImages {...props.manageMetaImagesPayload} />
            <ManageExternalLinks {...props.manageExternalLinksPayload} />

            <Grid container spacing={5} columns={{ xs: 4, sm: 8, md: 12 }} display="flex">

                {props.secureMediaItemPayload?.payload.albumPayloads.map(function (albumPayload) {
                    const artistPayload = props.secureMediaItemPayload?.payload.artistPayload
                    if (artistPayload) {
                        const albumWithArtistPayload: AlbumWithArtistPayload = {
                            albumPayload,
                            artistPayload
                        }

                        return (
                            <Grid item key={albumPayload.id} xs={4} sm={4} md={4} >
                                <AlbumSummary
                                    mediaToken={props.secureMediaItemPayload?.mediaToken || ''}
                                    payload={
                                        albumWithArtistPayload
                                    }
                                />
                            </Grid>
                        )

                    }
                })}
            </Grid>

            <CreateAlbumNameDialog {...props.createAlbumDialogPayload} />

            <div className="new-line right" style={{ marginTop: "1em" }}>
                <Button
                    variant="contained"
                    color="secondary"
                    type="button"
                    onClick={handleClickCancel}>
                    Cancel
                </Button>

                {showManageButtons() &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="button"
                        onClick={handleClickDelete}
                    >
                        Delete
                    </Button>
                }

                {showManageButtons() &&
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
        </form>
    )
}

export default Artist

