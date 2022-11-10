import { Add, PlayArrow } from '@mui/icons-material'
import { Button, Card, CardMedia } from '@mui/material'
import { useEffect, useState } from 'react'
import { AlbumWithArtistPayload } from '../../../media/music/rest/musicCalls'
import { SecureMediaPayload } from '../../../media/rest/secureMediaPayload'
import { backendUrl } from '../../utils/httpUtils'
import './AlbumSummary.css'

const AlbumSummary = (payload: SecureMediaPayload<AlbumWithArtistPayload>) => {

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithArtistPayload>>({
        ...payload
    })

    useEffect(() => {
        console.log('AlbumSummary', payload)

        setProps(payload)
    }, [payload])

    const albumArtImageUrl = (albumId: number): string => {
        return `${backendUrl('/media/music/album-art')}/${albumId}?mediaToken=${props.mediaToken}`
    }

    return (
        <Card className="album-summary" sx={{ width: 300 }}>
            <CardMedia
                component="img"
                image={albumArtImageUrl(props.payload.albumPayload.id)}
                height="300"
                className="cursor-pointer"
            />

            <div className="sticker title">
                <div className="album cursor-pointer">{props.payload.albumPayload.name}</div>
                <div className="artist cursor-pointer">{props.payload.artistPayload.name}</div>
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