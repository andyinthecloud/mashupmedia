import { Grid } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import AlbumSummary from "../../common/components/media/AlbumSummary"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { AlbumWithArtistPayload, getRandomAlbums } from "./rest/musicCalls"

const Albums = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithArtistPayload>[]>()

    useEffect(() => {
        getRandomAlbums(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
            }
        })

    }, [userToken])

    return (
        <div>

            <Grid container spacing={5} columns={{ xs: 4, sm: 8, md: 12 }} display="flex">

                {props?.map(function (albumWithArtistPayload) {

                    return (
                        <Grid
                            item
                            key={albumWithArtistPayload.payload.albumPayload.id}
                            xs={4}
                            sm={4}
                            md={4} >
                            <AlbumSummary
                                mediaToken={albumWithArtistPayload.mediaToken}
                                payload={albumWithArtistPayload.payload}
                            />
                        </Grid>
                    )
                })}
            </Grid>
        </div>
    )
}

export default Albums