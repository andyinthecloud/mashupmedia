import { Audiotrack } from "@mui/icons-material"
import { Button, FormControl, FormHelperText, InputLabel, MenuItem, Select } from "@mui/material"
import { t } from "i18next"
import { ChangeEvent, useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { NavLink, useNavigate, useParams } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { getDecades } from "../../common/utils/decadeUtils"
import { GENRE_AUTOMATIC, prepareUploadGenrePayloads } from "../../common/utils/genreUtils"
import { GenrePayload, getGenres } from "../../configuration/backend/metaCalls"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { artistImageUrl, ArtistWithAlbumsPayload, getArtist, ImageType } from "./rest/musicCalls"
import { UploadArtistTracksPayload } from "./rest/musicUploadCalls"
import "./UploadArtistTracks.css"
import { getLibraries, LibraryNameValuePayload } from "../../configuration/backend/libraryCalls"
import UploadTrackFiles, { UploadTrackFilesPayload } from "../../common/components/media/UploadTrackFiles"


type UploadArtistTracksPagePayload = {
    genrePayloads?: GenrePayload[]
    decades?: number[]
    uploadArtistTracksPayload?: UploadArtistTracksPayload
    artistWithAlbumsPayload?: SecureMediaPayload<ArtistWithAlbumsPayload>
    libraryNameValuePayloads?: LibraryNameValuePayload[]
    uploadTrackFilesPayload: UploadTrackFilesPayload

}

const UploadArtistTracks = () => {

    const navigate = useNavigate()
    const uploadFileRef = useRef<HTMLInputElement>(null);
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const { artistId } = useParams()

    const [props, setProps] = useState<UploadArtistTracksPagePayload>({
        uploadArtistTracksPayload: {
            artistId: 0,
            albumId: 0,
            libraryId: 0,
            genreIdName: GENRE_AUTOMATIC,
            decade: 0
        },
        uploadTrackFilesPayload: {
            selectFiles
        }
    })

    function selectFiles(fileList: FileList): void {
        setProps(p => ({
            ...p,
            uploadArtistTracksPayload: {
                ...p.uploadArtistTracksPayload,
                artistId: p.uploadArtistTracksPayload?.artistId || 0,
                albumId: p.uploadArtistTracksPayload?.albumId || 0,
                libraryId: p.uploadArtistTracksPayload?.libraryId || 0,
                fileList
            }
        }))
    }

    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                setProps(p => ({
                    ...p,
                    artistWithAlbumsPayload: response.parsedBody,
                    uploadArtistTracksPayload: {
                        ...p.uploadArtistTracksPayload,
                        albumId: p.uploadArtistTracksPayload?.albumId || 0,
                        libraryId: p.uploadArtistTracksPayload?.libraryId || 0,
                        artistId: +artistId
                    }
                }))
            })
        }
    }, [artistId])

    useEffect(() => {
        getGenres(userToken)
            .then(response => {
                const genrePayloads = prepareUploadGenrePayloads(response.parsedBody || [])
                setProps(p => ({
                    ...p,
                    genrePayloads,
                    decades: getDecades()
                }))
            })


        getLibraries(userToken)
            .then(response => {
                setProps(p => ({
                    ...p,
                    libraryNameValuePayloads: response.parsedBody
                }))
            })

    }, [userToken])

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {

        const files = e.target.files
        if (!files?.length) {
            return
        }

        setProps(p => ({
            ...p,
            uploadArtistTracksPayload: {
                ...p.uploadArtistTracksPayload,
                artistId: p.uploadArtistTracksPayload?.artistId || 0,
                albumId: p.uploadArtistTracksPayload?.albumId || 0,
                libraryId: p.uploadArtistTracksPayload?.libraryId || 0,
                fileList: files
            }
        }))

    }

    function handleClickSelectTracks(): void {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    function handleCancel(): void {
        const artistId = props.uploadArtistTracksPayload?.artistId
        if (artistId) {
            navigate('/music/artist/' + artistId)
        } else {
            navigate('/music/artists' + artistId)
        }
    }

    function selectedFiles() {
        const fileList = props.uploadArtistTracksPayload?.fileList

        if (!fileList) {
            return
        }

        return Array.from(fileList).map(file => (
            <div key={file.name}>{file.name}</div>
        ))

    }

    function handleChangeForm(name: string, value: string | number): void {
        setProps(p => ({
            ...p,
            uploadArtistTracksPayload: {
                ...p.uploadArtistTracksPayload,
                albumId: p.uploadArtistTracksPayload?.albumId || 0,
                artistId: p.uploadArtistTracksPayload?.artistId || 0,
                libraryId: p.uploadArtistTracksPayload?.libraryId || 0,
                [name]: value
            }
        }))
    }

    return (
        <form id="upload-artist-tracks">
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
                >
                    <InputLabel id="select-album-label">{t("uploadArtistTracks.library")}</InputLabel>
                    <Select
                        labelId="select-library-label"
                        name="libraryId"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        label={t("uploadArtistTracks.library")}
                        value={props?.uploadArtistTracksPayload?.libraryId}>
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
                >
                    <InputLabel id="select-album-label">{t("uploadArtistTracks.album")}</InputLabel>
                    <Select
                        labelId="select-album-label"
                        name="albumId"
                        onChange={e => handleChangeForm(e.target.name, e.target.value)}
                        label={t("uploadArtistTracks.album")}
                        value={props?.uploadArtistTracksPayload?.albumId}>
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
                        value={props?.uploadArtistTracksPayload?.genreIdName}>
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
                        value={props?.uploadArtistTracksPayload?.decade}
                        label={t("uploadArtistTracks.decade")}>
                        <MenuItem value="0"  >{t("uploadArtistTracks.metaTag")}</MenuItem>
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

            <div className="new-line">

                <input
                    style={{ display: 'none' }}
                    type="file"
                    multiple
                    accept="audio/*"
                    ref={uploadFileRef}
                    onChange={e => handleChangeFolder(e)}
                />

                <Button
                    className="edit-content"
                    variant="outlined"
                    endIcon={<Audiotrack />}
                    color="primary"
                    onClick={handleClickSelectTracks}
                >
                    {t('uploadArtistTracks.selectTracks')}
                </Button>

                {selectedFiles()}

            </div>

            <UploadTrackFiles {...props.uploadTrackFilesPayload}/>


            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    {t('label.cancel')}
                </Button>

                <Button variant="contained" color="primary" type="submit">
                    {t('label.ok')}
                </Button>
            </div>

        </form>
    )
}

export default UploadArtistTracks