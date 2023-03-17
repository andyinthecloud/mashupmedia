import { Add, PlayArrow } from "@mui/icons-material"
import { Card, CardContent, CardMedia, IconButton, List, ListItem, ListItemText } from "@mui/material"
import React, { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useParams } from "react-router-dom"
import ImagePopover, { ImagePopoverPayload } from "../../common/components/ImagePopover"
import { addNotification, NotificationType } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { play } from "./features/playMusicSlice"
import { albumArtImageUrl, AlbumWithTracksAndArtistPayload, getAlbum, ImageType } from "./rest/musicCalls"
import { addAlbum, playAlbum } from "./rest/playlistCalls"

const Album = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const { albumId } = useParams()

    const [props, setProps] = useState<SecureMediaPayload<AlbumWithTracksAndArtistPayload>>({
        mediaToken: "",
        payload: {
            albumPayload: {
                id: 0,
                name: ""
            },
            artistPayload: {
                id: 0,
                indexLetter: "",
                name: ""
            },
            trackPayloads: []
        }
    })

    useEffect(() => {
        if (albumId) {
            getAlbum(+albumId, userToken).then(response => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)

                    setImagePopoverPayload({
                        imageSource: albumArtImageUrl(
                            response.parsedBody.payload.albumPayload.id,
                            ImageType.ORIGINAL,
                            response.parsedBody.mediaToken),
                        timestamp: Date.now()
                    })
                }
            })
        }

    }, [albumId, userToken])

    const albumIdAsNumber = (): number => {
        if (props) {
            return props.payload.albumPayload.id;
        } else {
            return 0
        }
    }

    const [imagePopoverPayload, setImagePopoverPayload] = useState<ImagePopoverPayload>()

    const handleImagePopover = (event: React.MouseEvent<HTMLElement>) => {
        setImagePopoverPayload(p => ({
            ...p,
            anchorELement: event.currentTarget,
            timestamp: Date.now()
        }))
    }


    const dispatch = useDispatch()

    const handlePlay = (albumId: number): void => {
        playAlbum(albumId, userToken).then((response) => {
            if (response.ok) {
                dispatch(
                    play()
                )
                addNotification({
                    message: "Added to playlist",
                    notificationType: NotificationType.SUCCESS
                })
            }
        })
    }

    const handleAdd = (albumId: number): void => {
        addAlbum(albumId, userToken).then((response) => {
            if (response.ok) {
                addNotification({
                    message: "Added to playlist",
                    notificationType: NotificationType.SUCCESS
                })
            }
        })
    }


    return (

        <Card>

            <CardMedia
                component="img"
                image={albumArtImageUrl(albumIdAsNumber(), ImageType.ORIGINAL, props?.mediaToken)}
                height="300"
                className="cursor-pointer"
                onClick={handleImagePopover}
            />

            <ImagePopover {...imagePopoverPayload} />

            <CardContent>
                <h1>{props.payload.albumPayload.name}</h1>
                <h2>{props.payload.artistPayload.name}</h2>

                <List>
                    {props.payload.trackPayloads.map(function (songPayload) {
                        return (
                            <ListItem
                                secondaryAction={
                                    <div>
                                        <IconButton
                                            edge="end"
                                            color="primary"
                                            onClick={() => handlePlay(props.payload.albumPayload.id)}>
                                            <PlayArrow />
                                        </IconButton>
                                        <IconButton
                                            edge="end"
                                            color="primary"
                                            onClick={() => handleAdd(props.payload.albumPayload.id)}>
                                            <Add />
                                        </IconButton>
                                    </div>
                                }

                                key={songPayload.id}>


                                <ListItemText
                                    primary={songPayload.name}
                                    secondary={`${songPayload.minutes} min ${songPayload.seconds} sec`}
                                />
                            </ListItem>
                        )
                    })}
                </List>
            </CardContent>



        </Card>
    )

}

export default Album