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
import "./EditArtist.css"
import { artistImageUrl, ArtistWithAlbumsPayload, deleteArtist, getArtist, ImageType, saveArtist } from "./rest/musicCalls"
import { MetaImagePayload, uploadArtistImages } from "./rest/musicUploadCalls"

type EditArtistPayload = {
    secureMediaItemPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    manageMetaImagesPayload: ManageMetaImagesPayload
    musicEditMenuPayload: MusicEditMenuPayload
    manageExternalLinksPayload: ManageExternalLinksPayload
    formValidation: FormValidation
}


const EditArtist = () => {
    const { artistId } = useParams()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const artistPayloadRef = useRef<SecureMediaPayload<ArtistWithAlbumsPayload>>()
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const FIELD_NAME = "name"

    const [props, setProps] = useState<EditArtistPayload>({
        manageMetaImagesPayload: {
            metaImagePayloads: [],
            updateMetaImages: updateMetaImages,
            uploadFiles: uploadMetaImages,
            getImageUrl: getImageUrl
        },
        musicEditMenuPayload: {
            addExternalLink: handleAddExternalLink,
            addImage: handleAddImage
        },
        manageExternalLinksPayload: {
            externalLinkPayloads: [],
            updateExternalLinks: updateExternalLinks,
        },
        formValidation: {
            fieldValidations: []
        }
    })


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

    function handleAddImage(): void {
        setProps(p => ({
            ...p,
            manageMetaImagesPayload: {
                ...p.manageMetaImagesPayload,
                triggerUploadImage: Date.now()
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

    function getImageUrl(id: number): string {
        return artistImageUrl(
            artistPayloadRef.current?.payload.artistPayload.id || 0,
            ImageType.ORIGINAL,
            artistPayloadRef.current?.mediaToken
            || '',
            id)
    }

    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                const secureMediaItemPayload = response.parsedBody
                artistPayloadRef.current = secureMediaItemPayload

                setProps(p => ({
                    ...p,
                    secureMediaItemPayload,
                    manageMetaImagesPayload: {
                        ...p?.manageMetaImagesPayload,
                        metaImagePayloads: secureMediaItemPayload?.payload.artistPayload.metaImagePayloads || []
                    },
                    manageExternalLinksPayload: {
                        ...p.manageExternalLinksPayload,
                        externalLinkPayloads: secureMediaItemPayload?.payload.artistPayload.externalLinkPayloads || []
                    }
                }))
            })
        }
    }, [artistId])

    const handleClickCancel = (): void => {
        navigate('/music/artist/' + artistId)
    }

    const handleClickDelete = (): void => {
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
                        message: response.parsedBody?.value || '',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
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
                    navigate('/music/artist/' + artistId)
                }
            } else {
                const errorPayload = response.parsedBody?.errorPayload
                setProps(p => ({
                    ...p,
                    formValidation: {
                        fieldValidations: p.formValidation.fieldValidations.concat(
                            toFieldValidations(errorPayload)
                        )
                    }
                }))


                dispatch(
                    addNotification({
                        message: t("error.validation"),
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
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
                    albumPayloads: p.secureMediaItemPayload?.payload.albumPayloads || [],
                    artistPayload: {
                        ...p.secureMediaItemPayload?.payload.artistPayload,
                        id: p.secureMediaItemPayload?.payload.artistPayload.id || 0,
                        name: p.secureMediaItemPayload?.payload.artistPayload.name || '',
                        [name]: value
                    }
                }
            }
        }))
    }

    return (
        <form id="edit-artist">
            <div className="title">
                <h1>{t("editArtist.title")}</h1>

                <MusicEditMenu {...props.musicEditMenuPayload} />
            </div>

            <div className="new-line">
                <FormControl
                    fullWidth

                >
                    <TextField
                        required
                        label={t("editArtist.name")}
                        value={props.secureMediaItemPayload?.payload.artistPayload.name || ""}
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
                        label={t("editArtist.profile")}
                        multiline
                        name="profile"
                        value={props.secureMediaItemPayload?.payload.artistPayload.profile || ""}
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

export default EditArtist