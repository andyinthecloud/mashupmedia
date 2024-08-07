import Grid from '@mui/material/Unstable_Grid2';
import { t } from "i18next";
import { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover";
import CreateAlbumNameDialog, { CreateAlbumNameDialogPageload } from "../../common/components/dialogs/CreateAlbumNameDialog";
import AlbumSummary from '../../common/components/media/AlbumSummary';
import MusicMetaMenu, { MusicMetaMenuPagePayload } from "../../common/components/menus/MusicMetaMenu";
import { RootState } from '../../common/redux/store';
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import './Artist.css';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, ImageType, artistImageUrl, getArtist } from './rest/musicCalls';
import { isContentEditor } from "../../common/utils/adminUtils";

type ArtistPagePayload = {
    secureMediaItemPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    artistImagePopover: ImagePopoverPayload
    createAlbumDialogPayload: CreateAlbumNameDialogPageload
    musicMetaMenuPagePayload: MusicMetaMenuPagePayload
}

const Artist = () => {
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const { artistId } = useParams()
    const artistPayloadRef = useRef<SecureMediaPayload<ArtistWithAlbumsPayload>>()
    const navigate = useNavigate()

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
        artistImagePopover: {
            source: '',
            trigger: 0
        },
        createAlbumDialogPayload: {
            artistId: 0
        },
        musicMetaMenuPagePayload: {
            editor: false,
            edit: handleEdit,
            editLabel: t("editArtist.menuLink"),
            uploadTracks: handleUploadTracks,
            addAlbum: handleAddAlbum
        }
    })

    function handleUploadTracks(): void {
        console.log("handleUploadTracks")

    }

    function handleEdit(): void {
        navigate("/music/artist/edit/" + artistPayloadRef.current?.payload.artistPayload.id)
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

    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    const secureMediaItemPayload = response.parsedBody
                    artistPayloadRef.current = secureMediaItemPayload
                    const editor = isEditor()
                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload,
                        createAlbumDialogPayload: {
                            ...p.createAlbumDialogPayload,
                            artistId: +artistId
                        },
                        musicMetaMenuPagePayload: {
                            ...p.musicMetaMenuPagePayload,
                            artistId: +artistId,
                            editor
                        }
                    }))

                }
            })
        }
    }, [artistId])

    function isEditor(): boolean {
        return isContentEditor(artistPayloadRef.current?.payload.artistPayload.userPayload, userPolicyPayload)
    }

    function handleArtistImagePopover(imageId: number) {
        const source = artistImageUrl(
            props.secureMediaItemPayload?.payload.artistPayload.id || 0,
            ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
            imageId
        )

        setProps(p => ({
            ...p,
            artistImagePopover: {
                source,
                trigger: Date.now()
            }
        }))
    }

    function getDefaultImageId(): number {
        return props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads?.length ? props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads[0].id : 0
    }

    return (
        <div id='artist'>

            <ImagePopover {...props.artistImagePopover} />

            <div className="title">

                <div>
                    <img
                        src={artistImageUrl(
                            props.secureMediaItemPayload?.payload.artistPayload.id || 0,
                            ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
                            getDefaultImageId()
                        )}
                        onClick={() => handleArtistImagePopover(getDefaultImageId())}
                    />

                    <h1>{props.secureMediaItemPayload?.payload.artistPayload.name}</h1>
                </div>

                <MusicMetaMenu {...props.musicMetaMenuPagePayload} />


            </div>

            <div className="profile">

                {props.secureMediaItemPayload?.payload.artistPayload.profile &&
                    <div>{props.secureMediaItemPayload?.payload.artistPayload.profile}</div>
                }
            </div>


            <div className="images">
                {props.secureMediaItemPayload?.payload.artistPayload.metaImagePayloads?.map((metaImage, index) => {

                    if (index > 0) {
                        return (
                            <div key={metaImage.id}>
                                <img
                                    src={artistImageUrl(
                                        props.secureMediaItemPayload?.payload.artistPayload.id || 0,
                                        ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
                                        metaImage.id
                                    )}
                                    onClick={() => handleArtistImagePopover(metaImage.id)}
                                />
                            </div>
                        )
                    }
                })}
            </div>

            <div className="external-links">
                {props.secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads?.map((externalLink) => {
                    return (
                        <div key={externalLink.id}>
                            <a href={externalLink.link} target="external">{externalLink.name}</a>
                        </div>
                    )
                })}
            </div>

            <Grid container spacing={10} className="albums">

                {props.secureMediaItemPayload?.payload.albumPayloads.map(function (albumPayload) {
                    const artistPayload = props.secureMediaItemPayload?.payload.artistPayload
                    if (artistPayload) {
                        const albumWithArtistPayload: AlbumWithArtistPayload = {
                            albumPayload,
                            artistPayload
                        }

                        return (

                            <Grid sm={12} md={4} key={albumPayload.id}>
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

        </div>
    )
}

export default Artist

