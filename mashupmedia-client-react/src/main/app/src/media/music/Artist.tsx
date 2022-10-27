import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { RootState } from '../../common/redux/store';
import SecureImage from '../SecureImage';
import { ArtistWithAlbumsPayload, getArtist } from './rest/musicCalls';

const Artist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { artistId } = useParams()

    const [props, setProps] = useState<ArtistWithAlbumsPayload>()

    const albumArtUri = '/api/music/albums/album-art/'


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
        <div>
            <h1>{props?.artistPayload.name}</h1>

            <div>
                {props?.albumPayloads.map(function (albumPayload) {
                    return (
                        <div key={albumPayload.id}>
                            <h2>{albumPayload.name}</h2>

                            <SecureImage path={albumArtUri + albumPayload.id} />


                        </div>
                    )
                })}
            </div>



        </div>
    )

}

export default Artist
