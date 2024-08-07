import { Button, FormControl, TextField } from "@mui/material"
import { t } from "i18next"
import { ChangeEvent, useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useNavigate, useParams } from "react-router-dom"
import MusicEditMenu, { MusicEditMenuPayload } from "../../common/components/menus/MusicEditMenu"
import ManageExternalLinks, { ManageExternalLinksPayload } from "../../common/components/meta/ManageExternalLinks"
import ManageMetaImages, { ManageMetaImagesPayload } from "../../common/components/meta/ManageMetaImages"
import { addNotification, NotificationType } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { FormValidation, hasFieldError, toFieldValidations, translateFieldErrorMessage } from "../../common/utils/formValidationUtils"
import { ExternalLinkPayload } from "../rest/mediaCalls"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import "./EditAlbum.css"
import { albumArtImageUrl, AlbumWithTracksAndArtistPayload, deleteAlbum, getAlbum, ImageType, saveAlbum } from "./rest/musicCalls"
import { MetaImagePayload, uploadAlbumImages } from "./rest/musicUploadCalls"

type EditAlbumPayload = {
    secureMediaItemPayload?: SecureMediaPayload<AlbumWithTracksAndArtistPayload>
    musicEditMenuPayload: MusicEditMenuPayload
    manageMetaImagesPayload: ManageMetaImagesPayload
    manageExternalLinksPayload: ManageExternalLinksPayload
    formValidation: FormValidation
}

const EditAlbum = () => {

    const { albumId } = useParams()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const albumWithTracksAndArtistPayloadRef = useRef<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>()
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const FIELD_NAME = "name"

    const [props, setProps] = useState<EditAlbumPayload>({
        musicEditMenuPayload: {
            addExternalLink: handleAddExternalLink,
            addImage: handleAddImage
        },
        manageExternalLinksPayload: {
            externalLinkPayloads: [],
            updateExternalLinks: handleUpdateExternalLinks,
        },
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getMetaImageUrl,
        },
        formValidation: {
            fieldValidations: []
        }
    })

    useEffect(() => {
        if (albumId) {
            getAlbum(+albumId, userToken).then(response => {
                if (response.ok && response.parsedBody) {

                    const secureMediaItemPayload = response.parsedBody
                    albumWithTracksAndArtistPayloadRef.current = secureMediaItemPayload
                    const externalLinkPayloads = secureMediaItemPayload.payload.albumPayload.externalLinkPayloads || []
                    const metaImagePayloads = secureMediaItemPayload.payload.albumPayload.metaImagePayloads || []

                    setProps(p => ({
                        ...p,
                        secureMediaItemPayload,
                        manageMetaImagesPayload: {
                            ...p.manageMetaImagesPayload,
                            metaImagePayloads,
                        },
                        manageExternalLinksPayload: {
                            ...p.manageExternalLinksPayload,
                            externalLinkPayloads
                        }                    
                    }))

                }
            })
        }


    }, [albumId])

    function getMetaImageUrl(id: number): string {
        return albumArtImageUrl(
            albumWithTracksAndArtistPayloadRef.current?.payload.albumPayload.id || 0,
            ImageType.ORIGINAL,
            albumWithTracksAndArtistPayloadRef.current?.mediaToken || '',
            id)
    }

    function uploadMetaImages(files: FileList): void {
        console.log("uploadMetaImages: files", files)
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

    function handleAddImage(): void {
        setProps(p => ({
            ...p,
            manageMetaImagesPayload: {
                ...p.manageMetaImagesPayload,
                triggerUploadImage: Date.now()
            }
        }))
    }


    function handleUpdateExternalLinks(externalLinkPayloads: ExternalLinkPayload[]): void {
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

    function handleAddExternalLink(): void {
        setProps(p => ({
            ...p,
            manageExternalLinksPayload: {
                ...p.manageExternalLinksPayload,
                triggerAddExternalLink: Date.now()
            }
        }))
    }


    function handleChangeTextField(e: ChangeEvent<HTMLInputElement>): void {

        if (props.formValidation.fieldValidations) {
            setProps(p => ({
                ...p,
                formValidation: {
                    ...p.formValidation,
                    fieldValidations: []
                }
            }))
        }

        const name = e.target.name
        const value = e.target.value

        setProps(p => ({
            ...p,
            secureMediaItemPayload: {
                ...p.secureMediaItemPayload,
                mediaToken: p.secureMediaItemPayload?.mediaToken || '',
                payload: {
                    ...p.secureMediaItemPayload?.payload,
                    trackPayloads: p.secureMediaItemPayload?.payload.trackPayloads || [],
                    albumPayload: {
                        ...p.secureMediaItemPayload?.payload.albumPayload,
                        id: p.secureMediaItemPayload?.payload.albumPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.albumPayload.name || "",
                        [name]: value
                    },
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || ""
                    }



                }
            }
        }))
    }

    const handleClickCancel = (): void => {
        navigate('/music/album/' + albumId)
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
                    navigate('/music/album/' + albumId)
                    dispatch(
                        addNotification({
                            message: t('label.albumSaved'),
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                } else {
                    const errorPayload = response.parsedBody?.errorPayload
                    setProps(p => ({
                        ...p,
                        formValidation: {
                            fieldValidations: p.formValidation.fieldValidations.concat(
                                ...toFieldValidations(errorPayload)
                            )
                        }
                    }))


                    dispatch(
                        addNotification({
                            message: t('error.validation'),
                            notificationType: NotificationType.ERROR
                        })
                    )

                }
            })
    }

    return (
        <form id="edit-album">

            <div className="title">
                <h1>{t("editAlbum.title")}</h1>
                <MusicEditMenu {...props.musicEditMenuPayload} />
            </div>

            <div className="new-line">
                <FormControl
                    fullWidth

                >
                    <TextField
                        required
                        label={t("editAlbum.name")}
                        value={props.secureMediaItemPayload?.payload.albumPayload.name || ""}
                        name={FIELD_NAME}
                        onChange={handleChangeTextField}
                        error={hasFieldError(FIELD_NAME, props.formValidation)}
                        helperText={translateFieldErrorMessage(FIELD_NAME, props.formValidation)}
                    />
                </FormControl>
            </div>

            <div className="new-line">
                <FormControl
                    fullWidth
                >
                    <TextField
                        label={t("editAlbum.summary")}
                        multiline
                        name="summary"
                        value={props.secureMediaItemPayload?.payload.albumPayload.summary || ""}
                        onChange={handleChangeTextField}
                    />
                </FormControl>
            </div>

            <ManageMetaImages {...props.manageMetaImagesPayload} />

            <ManageExternalLinks {...props.manageExternalLinksPayload} />

            <div className="new-line right" style={{ marginTop: "1em" }}>
                <Button
                    variant="contained"
                    color="secondary"
                    type="button"
                    onClick={handleClickCancel}
                >
                    Cancel
                </Button>

                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                    onClick={handleClickDelete}
                >
                    Delete
                </Button>

                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                    onClick={handleClickSave}
                >
                    Save
                </Button>

            </div>


        </form>
    )
}

export default EditAlbum