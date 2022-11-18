import { Add, PlayArrow } from '@mui/icons-material'
import { Button, Card, CardMedia } from '@mui/material'
import { useEffect, useState } from 'react'
import { useNavigate } from "react-router-dom"
import { AlbumWithArtistPayload } from '../../../media/music/rest/musicCalls'
import { SecureMediaPayload } from '../../../media/rest/secureMediaPayload'
import { backendUrl } from '../../utils/httpUtils'
import './AlbumSummary.css'

const AlbumSummary = (payload: SecureMediaPayload<AlbumWithArtistPayload>) => {

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithArtistPayload>>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])

    const albumArtImageUrl = (albumId: number): string => {
        return `${backendUrl('/media/music/album-art')}/${albumId}?mediaToken=${props.mediaToken}`
    }

    const navigate = useNavigate()

    const handleAlbumClick = () => {
        navigate("/music/album/" + props.payload.albumPayload.id)
    }

    const handleArtistClick = () => {
        navigate("/music/artist/" + props.payload.artistPayload.id)
    }

    return (
        <Card className="album-summary" sx={{ width: 300 }}>
            <CardMedia
                component="img"
                image={albumArtImageUrl(props.payload.albumPayload.id)}
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
                <Button variant="contained" startIcon={<PlayArrow />}>
                    Play
                </Button>
                <Button variant="contained" startIcon={<Add />}>
                    Add
                </Button>

            </div>

        </Card>


    )

}

export default AlbumSummary