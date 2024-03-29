import { Add, PlayArrow } from '@mui/icons-material'
import { Button, Card, CardMedia } from '@mui/material'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { loadTrack } from "../../../media/music/features/playMusicSlice"
import { AlbumWithArtistPayload, ImageType, albumArtImageUrl } from '../../../media/music/rest/musicCalls'
import { playlistNotification } from "../../../media/music/rest/playlistActionUtils"
import { addAlbum, playAlbum } from "../../../media/music/rest/playlistActionCalls"
import { SecureMediaPayload } from '../../../media/rest/secureMediaPayload'
import { NotificationType, addNotification } from "../../notification/notificationSlice"
import { RootState } from "../../redux/store"
import './AlbumSummary.css'

const AlbumSummary = (payload: SecureMediaPayload<AlbumWithArtistPayload>) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithArtistPayload>>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])


    const navigate = useNavigate()

    const handleAlbumClick = () => {
        navigate("/music/album/" + props.payload.albumPayload.id)
    }

    const handleArtistClick = () => {
        navigate("/music/artist/" + props.payload.artistPayload.id)
    }

    const dispatch = useDispatch()

    const handlePlay = (albumId: number): void => {
        playAlbum(albumId, userToken).then((response) => {
            if (response.ok) {
                dispatch(
                    loadTrack({})
                )

                dispatch(
                    playlistNotification(response.parsedBody?.payload)
                )
            }
        })
    }

    const handleAdd = (albumId: number): void => {
        navigate("/playlists/music/select?albumId=" + albumId)
    }

    return (
        <Card className="album-summary">
            <CardMedia
                component="img"
                image={albumArtImageUrl(props.payload.albumPayload.id, ImageType.THUMBNAIL, props.mediaToken)}
                height="300"
                className="cursor-pointer"
                onClick={handleAlbumClick}
            />

            <div className="sticker title">
                <div
                    className="album cursor-pointer"
                    onClick={handleAlbumClick}>
                    {props.payload.albumPayload.name}</div>
                <div
                    className="artist cursor-pointer"
                    onClick={handleArtistClick}
                >{props.payload.artistPayload.name}</div>
            </div>

            <div className="controls">
                <Button
                    variant="contained"
                    startIcon={<PlayArrow />}
                    onClick={() => handlePlay(props.payload.albumPayload.id)}>
                    Play
                </Button>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleAdd(props.payload.albumPayload.id)}>
                    Add
                </Button>
            </div>

        </Card>
    )

}

export default AlbumSummary