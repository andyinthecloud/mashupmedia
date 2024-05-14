import { AddAPhoto, Edit, Mic, MoreVert, OpenInNew } from "@mui/icons-material";
import { Button, Grid, IconButton, Menu, MenuItem } from '@mui/material';
import { ChangeEvent, useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import AlbumSummary from '../../common/components/media/AlbumSummary';
import { RootState } from '../../common/redux/store';
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import './Artist.css';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, ImageType, albumArtImageUrl, artistImageUrl, deleteArtist, getArtist, saveArtist } from './rest/musicCalls';
import EditLinkDialog, { EditLinkDialogPageload as EditLinkDialogPayload } from "../../common/components/dialogs/EditLinkDialog";
import { ExternalLinkPayload } from "../rest/mediaCalls";
import LinkMenu, { LinkMenuPayload } from "../../common/components/menus/LinkMenu";
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog";
import { useDispatch } from "react-redux";
import { NotificationType, addNotification } from "../../common/notification/notificationSlice";
import { uploadArtistImages } from "./rest/musicUploadCalls";

type ArtistPagePayload = {
    secureMediaItemPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    editArtistNameDialogPayload: EditTextDialogPayload
    editExternalLinkDialogPayload: EditLinkDialogPayload
    editArtistProfileDialogPayload: EditTextDialogPayload
    linkMenuPayload: LinkMenuPayload
}

const Artist = () => {
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const { artistId } = useParams()
    const uploadFileRef = useRef<HTMLInputElement>(null);

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
        editExternalLinkDialogPayload: {
            dialogPayload: {
                open: false,
                title: 'Edit external link',
                payload: {
                    id: 0,
                    link: '',
                    name: '',
                    rank: 0
                },
                updatePayload: updateExternalLink
            }
        },
        linkMenuPayload: {
            anchorElement: null,
            open: false,
            editLink: openEditExternalLinkDialog,
            deleteLink: deleteExternalLink,
            moveTop: moveExternalLinkTop,
            moveUpOne: moveExternalLinkUpOne,
            moveDownOne: moveExternalLinkDownOne,
            moveBottom: moveExternalLinkBottom
        }
    })

    const externalLinksRef = useRef<ExternalLinkPayload[]>([])

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

    function updateExternalLink(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = addExternalLinkPayload(externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    function addExternalLinkPayload(externalLinkPayload: ExternalLinkPayload): ExternalLinkPayload[] {
        const externalLinkPayloads = externalLinksRef.current
        console.log('addExternalLinkPayload: beginning externalLinkPayloads = ', externalLinkPayloads)
        if (!externalLinkPayload.name && !externalLinkPayload.link) {
            return externalLinkPayloads
        }

        const index = externalLinkPayloads.findIndex(item => item.rank == externalLinkPayload.rank)
        console.log('addExternalLinkPayload: index = ', index)
        if (index < 0) {
            externalLinkPayloads.push(externalLinkPayload)
            console.log('addExternalLinkPayload: in if after push externalLinkPayloads = ', externalLinkPayloads)
            return externalLinkPayloads
        }

        externalLinkPayloads[index] = externalLinkPayload
        return externalLinkPayloads
    }

    function setExternalLinks(externalLinkPayloads: ExternalLinkPayload[]): void {
        externalLinkPayloads.map((externalLinkPayload, index) => {
            externalLinkPayload.rank = index
        })

        setProps(p => ({
            ...p,
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                dialogPayload: {
                    ...p.editExternalLinkDialogPayload.dialogPayload,
                    open: false
                }
            },
            linkMenuPayload: {
                ...p.linkMenuPayload,
                open: false
            },
            secureMediaItemPayload: {
                ...p.secureMediaItemPayload,
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        externalLinkPayloads,
                        metaImageRanks: p.secureMediaItemPayload?.payload.artistPayload.metaImageRanks
                    },
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || []

                }
            }
        }))

    }

    function deleteExternalLink(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = externalLinksRef.current
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        setExternalLinks(externalLinkPayloads)
    }

    function moveExternalLinkTop(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = externalLinksRef.current
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        externalLinkPayload.rank = 0
        externalLinkPayloads.splice(0, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    function moveExternalLinkUpOne(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = externalLinksRef.current
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank - 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    function moveExternalLinkDownOne(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = externalLinksRef.current
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank + 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    function moveExternalLinkBottom(externalLinkPayload: ExternalLinkPayload): void {
        const externalLinkPayloads = externalLinksRef.current
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayloads.length - 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank + 1, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    function openEditExternalLinkDialog(externalLinkPayload: ExternalLinkPayload) {

        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        const index = externalLinkPayloads.findIndex(
            item => item.rank === externalLinkPayload.rank
        )

        externalLinkPayloads[index] = externalLinkPayload

        setProps(p => ({
            ...p,
            linkMenuPayload: {
                ...p.linkMenuPayload,
                open: false
            },
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                dialogPayload: {
                    ...p.editExternalLinkDialogPayload.dialogPayload,
                    open: true,
                    payload: {
                        id: externalLinkPayload.id,
                        name: externalLinkPayload.name,
                        rank: externalLinkPayload.rank,
                        link: externalLinkPayload.link
                    }
                }
            }
        }))
    }



    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload: response.parsedBody
                    }))

                    externalLinksRef.current = response.parsedBody.payload.artistPayload.externalLinkPayloads || []
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

    const openNewExternalLinkDialog = () => {
        const rank = externalLinksRef.current.length || 0
        setProps(p => ({
            ...p,
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                dialogPayload: {
                    ...p.editExternalLinkDialogPayload.dialogPayload,
                    open: true,
                    payload: {
                        id: 0,
                        name: '',
                        link: '',
                        rank: rank
                    }
                }
            }
        }))

    }

    const handleClickLinkMenuIcon = (anchorElement: HTMLElement, externalLinkPayload: ExternalLinkPayload) => {
        setProps(p => ({
            ...p,
            linkMenuPayload: {
                ...p.linkMenuPayload,
                anchorElement,
                open: true,
                externalLinkPayload
            }
        }))
    }

    const dispatch = useDispatch()
    const navigate = useNavigate()

    const handleClickDeleteArtist = (): void => {
        const artistId = props.secureMediaItemPayload?.payload.artistPayload.id
        if (!artistId) {
            return
        }

        deleteArtist(artistId).then(response => {
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
                        message: 'Error deleting artist.',
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
                dispatch(
                    addNotification({
                        message: 'Saved artist.',
                        notificationType: NotificationType.SUCCESS
                    })
                )
                navigate('/music/artists')
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

    const showManageButtons = (): boolean => {
        const userPayload = props.secureMediaItemPayload?.payload.artistPayload.userPayload
        if (!userPayload || !userPolicyPayload) {
            return false
        }

        return userPolicyPayload.administrator || userPayload.username === userPolicyPayload.username
    }

    const handleUploadImagesClick = (): void => {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {

        const files = e.target.files
        if (!files?.length) {
            return
        }

        const artistId = props.secureMediaItemPayload?.payload.artistPayload.id
        if (!artistId) {
            return
        }

        uploadArtistImages(artistId, files, userToken).then(response => {
            if (response.ok) {
                console.log("ok")
            } else {
                console.log("nok")

            }
        })


    }

    return (
        <form id='artist'>

            <EditTextDialog {...props.editArtistNameDialogPayload} />
            <EditTextDialog {...props.editArtistProfileDialogPayload} />
            <EditLinkDialog {...props.editExternalLinkDialogPayload} />
            <LinkMenu {...props.linkMenuPayload} />

            <div className="title">

                <img src={artistImageUrl(props.secureMediaItemPayload?.payload.artistPayload.id || 0, ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '')} />

                <h1>{props.secureMediaItemPayload?.payload.artistPayload.name || 'New artist'}</h1>
                <IconButton
                    color="secondary"
                    onClick={openEditNameDialog}
                >
                    <Edit />
                </IconButton>
            </div>


            <div className="profile">
                <div>{props.secureMediaItemPayload?.payload.artistPayload.profile || 'Add profile'}</div>
                <IconButton
                    color="secondary"
                    onClick={openEditProfileDialog}
                >
                    <Edit />
                </IconButton>
            </div>

            <div className="images">
                <input
                    style={{ display: 'none' }}
                    type="file"
                    multiple
                    accept="image/png, image/jpeg"
                    ref={uploadFileRef}
                    onChange={e => handleChangeFolder(e)}
                />

                {props.secureMediaItemPayload?.payload.artistPayload.metaImageRanks?.map(index => {
                    return (
                        <div key={index} className="item">
                            <img src={artistImageUrl(props.secureMediaItemPayload?.payload.artistPayload.id || 0, ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '')} />
                            <IconButton
                                className="more-icon"
                                color="secondary" >
                                <MoreVert />
                            </IconButton>
                        </div>
                    )
                })}

                <IconButton
                    color="secondary"
                    onClick={handleUploadImagesClick}
                >
                    <AddAPhoto />
                </IconButton>
            </div>


            <div className="external-links">



                {props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads?.map(externalLinkPayload => {
                    return (
                        <div className="item" key={externalLinkPayload.id}>

                            <a target="_blank" rel="noreferrer" href={externalLinkPayload.link}>
                                <OpenInNew
                                    className="icon"
                                    color="secondary" />
                                <span>{externalLinkPayload.name}</span>
                            </a>

                            <IconButton
                                onClick={(e) => handleClickLinkMenuIcon(e.currentTarget, externalLinkPayload)}>
                                <MoreVert />
                            </IconButton>

                        </div>
                    )
                })}

                <Button
                    style={{ float: "right" }}
                    onClick={openNewExternalLinkDialog}
                    color="secondary"
                    variant="outlined">Add link</Button>

            </div>

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
                        onClick={handleClickDeleteArtist}
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
