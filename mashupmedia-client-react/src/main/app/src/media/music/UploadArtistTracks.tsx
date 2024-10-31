import { Button, FormControl, FormHelperText, InputLabel, MenuItem, Select } from "@mui/material"
import { t } from "i18next"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { NavLink, useLocation, useNavigate } from "react-router-dom"
import UploadFiles, { FileType, UploadFilesPayload } from "../../common/components/media/UploadFiles"
import { addNotification, NotificationType } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { getDecades } from "../../common/utils/decadeUtils"
import { FieldValidation, FormValidationPayload, hasFieldError } from "../../common/utils/formValidationUtils"
import { GENRE_AUTOMATIC, prepareUploadGenrePayloads } from "../../common/utils/genreUtils"
import { getLibraries, LibraryNameValuePayload } from "../../configuration/backend/libraryCalls"
import { GenrePayload, getGenres } from "../../configuration/backend/metaCalls"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { artistImageUrl, ArtistWithAlbumsPayload, getArtist, ImageType } from "./rest/musicCalls"
import { uploadArtistTracks, UploadArtistTracksPayload } from "./rest/musicUploadCalls"
import "./UploadArtistTracks.css"


type UploadArtistTracksPagePayload = {
    genrePayloads?: GenrePayload[]
    decades?: number[]
    // uploadArtistTracksPayload?: UploadArtistTracksPayload
    artistWithAlbumsPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    libraryNameValuePayloads?: LibraryNameValuePayload[]
    uploadFilesPayload: UploadFilesPayload
    uploadingTracks?: boolean
    formValidationPayload: FormValidationPayload<UploadArtistTracksPayload>

}

const UploadArtistTracks = () => {
    const { state } = useLocation()
    const navigate = useNavigate()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const dispatch = useDispatch()

    const [props, setProps] = useState<UploadArtistTracksPagePayload>({
        uploadFilesPayload: {
            selectFiles,
            fileType: FileType.AUDIO
        },
        formValidationPayload: {
            formValidation: {
                fieldValidations: []
            },
            payload: {
                albumId: 0,
                libraryId: 0,
                genreIdName: GENRE_AUTOMATIC,
                decade: 0
            }
        }
    })

    function selectFiles(files: File[]): void {
        setProps(p => ({
            ...p,
            formValidationPayload: {
                ...p.formValidationPayload,
                payload: {
                    ...p.formValidationPayload.payload,
                    albumId: p.formValidationPayload.payload?.albumId || 0,
                    libraryId: p.formValidationPayload.payload?.libraryId || 0,
                    files

                }
            }
        }))
    }

    useEffect(() => {
        if (!state) {
            return
        }

        const artistId: number = state.artistId
        const albumId: number = state.albumId

        getArtist(+artistId, userToken).then(response => {
            setProps(p => ({
                ...p,
                artistWithAlbumsPayload: response.parsedBody,
                formValidationPayload: {
                    ...p.formValidationPayload,
                    payload: {
                        ...p.formValidationPayload.payload,
                        albumId,
                        libraryId: p.formValidationPayload.payload?.libraryId || 0,
                    }
                }
            }))
        })
    }, [state])

    useEffect(() => {
        getGenres(userToken)
            .then(response => {
                const genrePayloads = prepareUploadGenrePayloads(response.parsedBody || [])
                const decades = getDecades()
                setProps(p => ({
                    ...p,
                    genrePayloads,
                    decades,
                    formValidationPayload: {
                        ...p.formValidationPayload,
                        payload: {
                            ...p.formValidationPayload.payload,
                            albumId: p.formValidationPayload.payload?.albumId || 0,
                            libraryId: p.formValidationPayload.payload?.libraryId || 0,
                            genreIdName: genrePayloads.length ? genrePayloads[0].idName : ""
                        }
                    }
                }))
            })


        getLibraries(userToken)
            .then(response => {
                const libraryNameValuePayloads = response.parsedBody
                setProps(p => ({
                    ...p,
                    libraryNameValuePayloads,
                    formValidationPayload: {
                        ...p.formValidationPayload,
                        payload: {
                            ...p.formValidationPayload.payload,
                            albumId: p.formValidationPayload.payload?.albumId || 0,
                            libraryId: libraryNameValuePayloads?.length ? libraryNameValuePayloads[0].value : 0,
                        }
                    }
                }))
            })

    }, [userToken])

    function handleCancel(): void {
        const artistId = props.artistWithAlbumsPayload?.payload.artistPayload.id
        navigate('/music/artist/' + artistId)
    }

    function handleChangeForm(name: string, value: string | number): void {
        setProps(p => ({
            ...p,
            formValidationPayload: {
                ...p.formValidationPayload,
                payload: {
                    ...p.formValidationPayload.payload,
                    albumId: p.formValidationPayload.payload?.albumId || 0,
                    libraryId: p.formValidationPayload.payload?.libraryId || 0,
                    [name]: value

                }
            }
        }))
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        console.log("validate")
        validateForm()
        if (props.formValidationPayload.formValidation.fieldValidations.length) {
            return;
        }

        handleUpload()
    }

    function validateForm(): void {
        clearFieldValidationState()
        const payload = props.formValidationPayload.payload
        if (isNaN(payload.albumId)) {
            addInvalidField({
                name: "albumId",
                messageCode: t("uploadArtistTracks.error.album")
            })
        }

        if (!payload.files?.length) {
            addInvalidField({
                name: "files",
                messageCode: t("uploadArtistTracks.error.files")
            })

        }

    }

    function clearFieldValidationState(): void {
        const fieldValidations = props.formValidationPayload.formValidation.fieldValidations
        fieldValidations.splice(0, fieldValidations.length)
        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    function addInvalidField(fieldValidation: FieldValidation): void {
        const fieldValidations = props.formValidationPayload.formValidation.fieldValidations
        fieldValidations.push(fieldValidation)

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    function handleUpload(): void {
        const uploadTrackFilesPayload = props.formValidationPayload.payload
        if (!uploadTrackFilesPayload) {
            return
        }


        setProps(p => ({
            ...p,
            uploadingTracks: true
        }))


        uploadArtistTracks(uploadTrackFilesPayload, userToken)
            .then(response => {
                setProps(p => ({
                    ...p,
                    uploadingTracks: false
                }))

                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: t("uploadArtistTracks.uploaded"),
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/music/artist/' + props.artistWithAlbumsPayload?.payload.artistPayload.id)
                } else {
                    dispatch(
                        addNotification({
                            message: t(response.parsedBody?.errorPayload?.errorCode || "error.general"),
                            notificationType: NotificationType.ERROR
                        })
                    )
                }

            })
    }

    return (
        <form id="upload-artist-tracks" onSubmit={handleSubmit} noValidate>
            <h1>{t("uploadArtistTracks.title")}</h1>

            <div className="artist">

                <img
                    src={artistImageUrl(
                        props.artistWithAlbumsPayload?.payload.artistPayload.id || 0,
                        ImageType.ORIGINAL, props.artistWithAlbumsPayload?.mediaToken || '',
                        props.artistWithAlbumsPayload?.payload.artistPayload.metaImagePayloads?.length ? props.artistWithAlbumsPayload?.payload.artistPayload.metaImagePayloads[0].id : 0
                    )}

                />

                <div><NavLink to={"/music/artist/" + props.artistWithAlbumsPayload?.payload.artistPayload.id}>{props.artistWithAlbumsPayload?.payload.artistPayload.name}</NavLink></div>
            </div>

            <div className="new-line">
                <FormControl
                    fullWidth
                    sx={{
                        marginTop: "1em",
                    }}
                    required
                >
                    <InputLabel id="select-album-label">{t("uploadArtistTracks.library")}</InputLabel>
                    <Select
                        labelId="select-library-label"
                        name="libraryId"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        label={t("uploadArtistTracks.library")}
                        value={props?.formValidationPayload.payload?.libraryId || ""}
                    >
                        {props?.libraryNameValuePayloads?.map((libraryPayload) => (
                            <MenuItem
                                value={libraryPayload.value}
                                key={libraryPayload.value}>
                                {libraryPayload.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </div>



            <div className="new-line">
                <FormControl
                    fullWidth
                    sx={{
                        marginTop: "1em",
                    }}
                    required
                >
                    <InputLabel id="select-album-label">{t("uploadArtistTracks.album")}</InputLabel>
                    <Select
                        labelId="select-album-label"
                        name="albumId"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        label={t("uploadArtistTracks.album")}
                        value={props.formValidationPayload.payload?.albumId || ""}
                        error={hasFieldError("albumId", props.formValidationPayload.formValidation)}
                    >
                        {props?.artistWithAlbumsPayload?.payload.albumPayloads?.map((albumPayload) => (
                            <MenuItem
                                value={albumPayload.id}
                                key={albumPayload.id}>
                                {albumPayload.name}
                            </MenuItem>
                        ))}
                    </Select>
                    <FormHelperText>{t("uploadArtistTracks.albumHelp")}</FormHelperText>
                </FormControl>
            </div>

            <div className="new-line">
                <FormControl
                    fullWidth
                    sx={{
                        marginTop: "1em",
                    }}
                >
                    <InputLabel id="select-genre-label">{t("uploadArtistTracks.genre")}</InputLabel>
                    <Select
                        labelId="select-genre-label"
                        label={t("uploadArtistTracks.genre")}
                        name="genreIdName"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        value={props?.formValidationPayload.payload?.genreIdName || ""}
                    >
                        {props?.genrePayloads?.map((genrePayload) => (
                            <MenuItem
                                value={genrePayload.idName}
                                key={genrePayload.idName}>
                                {genrePayload.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </div>


            <div className="new-line">
                <FormControl
                    fullWidth
                >
                    <InputLabel id="select-decade-label">{t("uploadArtistTracks.decade")}</InputLabel>
                    <Select
                        labelId="select-decade-label"
                        name="decade"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        value="0"
                        label={t("uploadArtistTracks.decade")}>
                        <MenuItem value="0">{t("uploadArtistTracks.metaTag")}</MenuItem>
                        {props?.decades?.map(decade => (
                            <MenuItem
                                value={decade}
                                key={decade}>
                                {decade}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </div>

            <UploadFiles {...props.uploadFilesPayload} />
            {hasFieldError("files", props.formValidationPayload.formValidation) &&
                <div className="error">
                    {t("uploadArtistTracks.error.files")}
                </div>
            }

            <div className="new-line right">
                <Button
                    variant="contained"
                    color="secondary"
                    type="button"
                    onClick={handleCancel}>
                    {t('label.cancel')}
                </Button>

                {!props.uploadingTracks &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="submit">
                        {t('uploadArtistTracks.button.upload')}
                    </Button>
                }

                {props.uploadingTracks &&
                    <Button
                        disabled={true}
                        variant="contained"
                        color="primary"
                        type="button"
                    >
                        {t('uploadArtistTracks.button.uploading')}
                    </Button>
                }
            </div>

        </form>
    )
}

export default UploadArtistTracks