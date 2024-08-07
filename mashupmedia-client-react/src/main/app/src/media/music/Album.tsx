import { Add, PlayArrow } from "@mui/icons-material"
import { Button, Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import { t } from "i18next"
import { useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router-dom"
import CreateAlbumNameDialog, { CreateAlbumNameDialogPageload } from "../../common/components/dialogs/CreateAlbumNameDialog"
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover"
import MusicMetaMenu, { MusicMetaMenuPagePayload } from "../../common/components/menus/MusicMetaMenu"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { isContentEditor } from "../../common/utils/adminUtils"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import './Album.css'
import { loadTrack } from "./features/playMusicSlice"
import { AlbumWithTracksAndArtistPayload, ImageType, albumArtImageUrl, getAlbum } from "./rest/musicCalls"
import { playAlbum, playTrack } from "./rest/playlistActionCalls"


type AlbumPagePageload = {
    secureMediaItemPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    imagePopover: ImagePopoverPayload
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
        createAlbumDialogPayload: {
            artistId: 0
        },
        musicMetaMenuPagePayload: {
            editor: false,
            edit: handleEdit,
            editLabel: t("editAlbum.menuLink"),
            uploadTracks: handleUploadTracks,
            addAlbum: handleAddAlbum
        }
    })

    function handleUploadTracks(): void {
        console.log("handleUploadTracks")

    }

    function handleEdit(): void {
        navigate("/music/album/edit/" + albumId)
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

    function isEditor(): boolean {
        return isContentEditor(albumWithTracksAndArtistPayloadRef.current?.payload.artistPayload.userPayload, userPolicyPayload)
    }

    function handleAlbumImagePopover(imageId: number) {
        const source = albumArtImageUrl(
            props.secureMediaItemPayload?.payload.albumPayload.id || 0,
            ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
            imageId
        )

        setProps(p => ({
            ...p,
            imagePopover: {
                source,
                trigger: Date.now()
            }
        }))
    }

    return (

        <Card id="album">

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

                <div className="images">
                    {props.secureMediaItemPayload?.payload.albumPayload.metaImagePayloads?.map((metaImage, index) => {

                        if (index > 0) {
                            return (
                                <div key={metaImage.id}>
                                    <img
                                        src={albumArtImageUrl(
                                            props.secureMediaItemPayload?.payload.albumPayload.id || 0,
                                            ImageType.ORIGINAL, props.secureMediaItemPayload?.mediaToken || '',
                                            metaImage.id
                                        )}
                                        onClick={() => handleAlbumImagePopover(metaImage.id)}
                                    />
                                </div>
                            )
                        }
                    })}
                </div>

                <div className="external-links">
                    {props.secureMediaItemPayload?.payload.albumPayload.externalLinkPayloads?.map((externalLink) => {
                        return (
                            <div key={externalLink.id}>
                                <a href={externalLink.link} target="external">{externalLink.name}</a>
                            </div>
                        )
                    })}
                </div>

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

            </CardContent>

        </Card>
    )

}

export default Album