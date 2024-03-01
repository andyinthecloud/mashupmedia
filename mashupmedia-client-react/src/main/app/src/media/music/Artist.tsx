import { Edit, MoreVert, OpenInNew } from "@mui/icons-material";
import { Button, Grid, IconButton, Menu, MenuItem } from '@mui/material';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import EditTextDialog, { EditTextDialogPageload as EditTextDialogPayload } from "../../common/components/dialogs/EditTextDialog";
import AlbumSummary from '../../common/components/media/AlbumSummary';
import { RootState } from '../../common/redux/store';
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import './Artist.css';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, getArtist } from './rest/musicCalls';
import EditLinkDialog, { EditLinkDialogPageload as EditLinkDialogPayload } from "../../common/components/dialogs/EditLinkDialog";
import { ExternalLinkPayload } from "../rest/mediaCalls";
import LinkMenu, { LinkMenuPayload } from "../../common/components/menus/LinkMenu";

type ArtistPagePayload = {
    secureMediaItemPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    editArtistNameDialogPayload: EditTextDialogPayload
    editExternalLinkDialogPayload: EditLinkDialogPayload
    editArtistProfileDialogPayload: EditTextDialogPayload
    linkMenuPayload: LinkMenuPayload
}

const Artist = () => {
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const { artistId } = useParams()
    // const [props, setProps] = useState<SecureMediaPayload<ArtistWithAlbumsPayload>>()

    const updateArtistName = (text: string): void => {
        setProps(p => ({
            ...p,
            editArtistNameDialogPayload: {
                ...p.editArtistNameDialogPayload,
                open: false
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
                open: false
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

    const updateExternalLink = (index: number, name: string, link: string): void => {

        const externalLinkPayload: ExternalLinkPayload = {
            id: index || 0,
            link,
            name,
            rank: index
        }

        let externalLinkPayloads = props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads || []
        if (externalLinkPayloads.length) {
            externalLinkPayloads = externalLinkPayloads.splice(index, 0, externalLinkPayload)
        } else {
            externalLinkPayloads.push(externalLinkPayload)
        }

        setProps(p => ({
            ...p,
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                open: false
            },
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

    const openEditExternalLinkDialog = (externalLinkPayload: ExternalLinkPayload) => {
        setProps(p => ({
            ...p,
            linkMenuPayload: {
                ...p.linkMenuPayload,
                open: false
            },
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                open: true,
                name: externalLinkPayload.name,
                index: externalLinkPayload.rank,
                link: externalLinkPayload.link
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
            open: false,
            text: '',
            title: "Edit artist name",
            textFieldLabel: "Name",
            updateText: updateArtistName
        },
        editArtistProfileDialogPayload: {
            open: false,
            text: '',
            multiline: true,
            title: "Edit artist profile",
            textFieldLabel: "Profile",
            updateText: updateArtistProfile
        },
        editExternalLinkDialogPayload: {
            open: false,
            index: 0,
            link: '',
            name: '',
            title: 'Edit external link',
            updateLink: updateExternalLink
        },
        linkMenuPayload: {
            anchorElement: null,
            open: false,
            editLink: openEditExternalLinkDialog
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


    useEffect(() => {
        setProps(p => ({
            ...p,
            editArtistNameDialogPayload: props.editArtistNameDialogPayload
        }))
    }, [props.editArtistNameDialogPayload])

    const openEditNameDialog = (open: boolean) => {
        setProps(p => ({
            ...p,
            editArtistNameDialogPayload: {
                ...p.editArtistNameDialogPayload,
                open
            }
        }))
    }

    const openEditProfileDialog = (open: boolean) => {
        setProps(p => ({
            ...p,
            editArtistProfileDialogPayload: {                
                ...p.editArtistProfileDialogPayload,
                open
            }
        }))
    }

    const openNewExternalLinkDialog = (open: boolean) => {
        setProps(p => ({
            ...p,
            editExternalLinkDialogPayload: {
                ...p.editExternalLinkDialogPayload,
                open,
                name: '',
                index: 0,
                link: ''
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

    return (
        <form id='artist'>

            <EditTextDialog {...props.editArtistNameDialogPayload} />
            <EditTextDialog {...props.editArtistProfileDialogPayload} />
            <EditLinkDialog {...props.editExternalLinkDialogPayload} />
            <LinkMenu {...props.linkMenuPayload} />

            <p className="title">
                <h1>{props.secureMediaItemPayload?.payload.artistPayload.name || 'New artist'}</h1>
                <IconButton
                    color="secondary"
                    onClick={() => openEditNameDialog(true)}
                >
                    <Edit />
                </IconButton>
            </p>


            <p className="profile">
                <div>{props.secureMediaItemPayload?.payload.artistPayload.profile || 'Add profile'}</div>
                <IconButton
                    color="secondary"
                    onClick={() => openEditProfileDialog(true)}
                >
                    <Edit />
                </IconButton>
            </p>

            <p className="external-links">

                <Button
                    style={{ float: "right" }}
                    onClick={() => openNewExternalLinkDialog(true)}
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

            </p>

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

            <div className="new-line right">
                <Button
                    variant="contained"
                    color="secondary"
                    type="button">
                    Cancel
                </Button>

                <Button
                    variant="contained"
                    color="primary"
                    type="button">
                    Delete
                </Button>

                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                >
                    Save
                </Button>
            </div>
        </form>
    )
}

export default Artist
