import { Edit, MoreVert, OpenInNew } from "@mui/icons-material";
import { Button, Grid, IconButton, Menu, MenuItem } from '@mui/material';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import AlbumSummary from '../../common/components/media/AlbumSummary';
import { RootState } from '../../common/redux/store';
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import './Artist.css';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, deleteArtist, getArtist } from './rest/musicCalls';
import EditLinkDialog, { EditLinkDialogPageload as EditLinkDialogPayload } from "../../common/components/dialogs/EditLinkDialog";
import { ExternalLinkPayload } from "../rest/mediaCalls";
import LinkMenu, { LinkMenuPayload } from "../../common/components/menus/LinkMenu";
import EditTextDialog, { EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog";
import { useDispatch } from "react-redux";
import { NotificationType, addNotification } from "../../common/notification/notificationSlice";

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

    const updateArtistName = (text: string): void => {
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
                        name: text
                    }
                }
            }
        }))
    }

    const updateArtistProfile = (text: string): void => {
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
                        profile: text
                    }
                }
            }
        }))
    }

    const updateExternalLink = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = addExternalLinkPayload(externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    const addExternalLinkPayload = (externalLinkPayload: ExternalLinkPayload): ExternalLinkPayload[] => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads || []

        if (!externalLinkPayload.name && !externalLinkPayload.link) {
            return externalLinkPayloads
        }

        const index = externalLinkPayloads.findIndex(item => item.id == externalLinkPayload.id)
        if (index < 0) {
            externalLinkPayloads.push(externalLinkPayload)
            return externalLinkPayloads
        }

        externalLinkPayloads[index] = externalLinkPayload
        return externalLinkPayloads
    }

    const setExternalLinks = (externalLinkPayloads: ExternalLinkPayload[]): void => {
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
                mediaToken: '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        externalLinkPayloads
                    },
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || []

                }
            }
        }))

    }

    const deleteExternalLink = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        const updatedExternalLinkPayloads = externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        setExternalLinks(updatedExternalLinkPayloads)
    }

    const moveExternalLinkTop = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        externalLinkPayload.rank = 0
        externalLinkPayloads.splice(0, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    const moveExternalLinkUpOne = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank - 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    const moveExternalLinkDownOne = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank + 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    const moveExternalLinkBottom = (externalLinkPayload: ExternalLinkPayload): void => {
        const externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads
        if (!externalLinkPayloads) {
            return
        }

        externalLinkPayloads.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayloads.length - 1
        externalLinkPayload.rank = rank
        externalLinkPayloads.splice(rank, 0, externalLinkPayload)
        setExternalLinks(externalLinkPayloads)
    }

    const openEditExternalLinkDialog = (externalLinkPayload: ExternalLinkPayload) => {

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

    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload: response.parsedBody
                    }))
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

        const index = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads?.length || 0

        setProps(p => ({
            ...p,
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                dialogPayload: {
                    ...p.editExternalLinkDialogPayload.dialogPayload,
                    open: true,
                    payload: {
                        ...p.editExternalLinkDialogPayload.dialogPayload.payload,
                        id: index,
                        name: '',
                        link: '',
                        rank: index
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
        console.log("handleClickDeleteArtist");

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

    const showManageButtons = (): boolean => {
        const userPayload = props.secureMediaItemPayload?.payload.artistPayload.userPayload
        if (!userPayload || !userPolicyPayload) {
            return false
        }

        return userPolicyPayload.administrator || userPayload.username === userPolicyPayload.username        
    }

    return (
        <form id='artist'>

            <EditTextDialog {...props.editArtistNameDialogPayload} />
            <EditTextDialog {...props.editArtistProfileDialogPayload} />
            <EditLinkDialog {...props.editExternalLinkDialogPayload} />
            <LinkMenu {...props.linkMenuPayload} />

            <div className="title">
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

            <div className="external-links">


                <pre>{JSON.stringify(props.secureMediaItemPayload, null, 2)}</pre>

                <Button
                    style={{ float: "right" }}
                    onClick={openNewExternalLinkDialog}
                    color="secondary"
                    variant="outlined">Add link</Button>

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

            <div className="new-line right" style={{marginTop: "1em"}}>
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
                    >
                        Save
                    </Button>
                }

            </div>
        </form>
    )
}

export default Artist
