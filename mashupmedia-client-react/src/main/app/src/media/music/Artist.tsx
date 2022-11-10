import { Grid } from '@mui/material';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import AlbumSummary from '../../common/components/media/AlbumSummary';
import { RootState } from '../../common/redux/store';
import { SecureMediaPayload } from '../rest/secureMediaPayload';
import { AlbumWithArtistPayload, ArtistWithAlbumsPayload, getArtist } from './rest/musicCalls';

const Artist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { artistId } = useParams()

    const [props, setProps] = useState<SecureMediaPayload<ArtistWithAlbumsPayload>>()


    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)
                }
            })
        }

    }, [artistId, userToken])


    return (
        <div id='artist'>
            <h1>{props?.payload.artistPayload.name}</h1>

            <Grid container spacing={5} columns={{ xs: 4, sm: 8, md: 12 }} display="flex">

                {props?.payload.albumPayloads.map(function (albumPayload) {
                    const albumWithArtistPayload: AlbumWithArtistPayload = {
                        albumPayload, 
                        artistPayload: props.payload.artistPayload
                    }
                    return (
                        <Grid item key={albumPayload.id} xs={4} sm={4} md={4} >
                            <AlbumSummary
                                mediaToken={props.mediaToken}
                                payload={
                                    albumWithArtistPayload
                                }
                            />
                        </Grid>
                    )
                })}
            </Grid>
        </div>
    )

}

export default Artist
