import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { RootState } from '../../common/redux/store';
import { backendUrl } from '../../common/utils/httpUtils';
import { SecureTokenPayload } from '../rest/secureTokenPayload';
import { ArtistWithAlbumsPayload, getArtist } from './rest/musicCalls';

const Artist = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { artistId } = useParams()

    const [props, setProps] = useState<SecureTokenPayload<ArtistWithAlbumsPayload>>()


    useEffect(() => {
        if (artistId) {
            getArtist(+artistId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)
                }
            })
        }

    }, [artistId, userToken])

    const albumArtImageUrl = (albumId: number): string => {
        return `${backendUrl('/media/music/album-art')}/${albumId}?mediaToken=${props?.secureToken}` 
    }

    return (
        <div>
            <h1>{props?.payload.artistPayload.name}</h1>

            <div>
                {props?.payload.albumPayloads.map(function (albumPayload) {
                    return (
                        <div key={albumPayload.id}>
                            <h2>{albumPayload.name}</h2>

                            <img src={albumArtImageUrl(albumPayload.id)} />


                        </div>
                    )
                })}
            </div>



        </div>
    )

}

export default Artist
