import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate, useSearchParams } from "react-router-dom"
import { RootState } from "../../common/redux/store"
import { MediaSearchResultPayload, MusicSearchResultPayload, searchMedia } from "./rest/searchCalls"
import { IconButton, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material"
import { MashupMediaType, playAlbum, playArtist, playTrack } from "../music/rest/playlistActionCalls"
import { Add, Album, Audiotrack, Inbox, Person, PlayArrow } from "@mui/icons-material"
import { getTrackYearInBrackets } from "../music/utils/musicItemUtils"
import { useDispatch } from "react-redux"
import { loadTrack } from "../music/features/playMusicSlice"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"


type MediaSearchResultsPayload = {
    mediaSearchResultPayloads: MediaSearchResultPayload[]
}

enum MediaSearchResultType {
    TRACK, ALBUM, ARTIST, NONE
}

const MediaSearchResults = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [queryParameters] = useSearchParams()
    const [props, setProps] = useState<MediaSearchResultsPayload>()

    useEffect(() => {

        const searchText = queryParameters.get("search")
        if (!searchText) {
            return
        }

        searchMedia(searchText, userToken).then(response => {
            console.log("searchMedia", response.parsedBody)
            if (response.ok) {
                setProps({
                    mediaSearchResultPayloads: response.parsedBody || []
                })
            }
        })

    }, [userToken, queryParameters])

    const dispatch = useDispatch()
    const handlePlayItem = (mediaItemId: number, mediaSearchResultType: MediaSearchResultType): void => {

        if (mediaSearchResultType === MediaSearchResultType.TRACK) {
            playTrack(mediaItemId, userToken).then((response) => {
                if (response.ok) {
                    dispatch(
                        loadTrack({})
                    )
                    dispatch(
                        addNotification({
                            message: "Replaced playlist",
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                }
            })
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ALBUM) {
            playAlbum(mediaItemId, userToken).then((response) => {
                if (response.ok) {
                    dispatch(
                        loadTrack({})
                    )
                    dispatch(
                        addNotification({
                            message: "Replaced playlist",
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                }
            })
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ARTIST) {
            playArtist(mediaItemId, userToken).then((response) => {
                if (response.ok) {
                    dispatch(
                        loadTrack({})
                    )
                    dispatch(
                        addNotification({
                            message: "Replaced playlist",
                            notificationType: NotificationType.SUCCESS
                        })
                    )

                }
            })
            return
        }
    }

    const navigate = useNavigate()
    const handleAddItem = (mediaItemId: number, mediaSearchResultType: MediaSearchResultType): void => {
        if (mediaSearchResultType === MediaSearchResultType.TRACK) {
            navigate("/playlists/music/select?trackId=" + mediaItemId)
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ALBUM) {
            navigate("/playlists/music/select?albumId=" + mediaItemId)
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ARTIST) {
            navigate("/playlists/music/select?artistId=" + mediaItemId)
            return
        }
    }


    const handleSelectItem = (mediaItemId: number, mediaSearchResultType: MediaSearchResultType): void => {
        if (mediaSearchResultType === MediaSearchResultType.TRACK) {
            navigate("/music/album/" + mediaItemId)
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ALBUM) {
            navigate("/music/album/" + mediaItemId)
            return
        }

        if (mediaSearchResultType === MediaSearchResultType.ARTIST) {
            navigate("/music/artist/" + mediaItemId)
            return
        }
    }

    const getMediaSearchResultType = (musicSearchResultPayload: MusicSearchResultPayload): MediaSearchResultType => {
        if (musicSearchResultPayload.trackPayload) {
            return MediaSearchResultType.TRACK
        }

        if (musicSearchResultPayload.albumPayload) {
            return MediaSearchResultType.ALBUM
        }

        if (musicSearchResultPayload.artistPayload) {
            return MediaSearchResultType.ARTIST
        }

        return MediaSearchResultType.NONE

    }

    const displaySearchResult = (mediaSearchResultPayload: MediaSearchResultPayload, index: number) => {

        if (mediaSearchResultPayload.mashupMediaType === MashupMediaType.MUSIC) {
            const musicSearchResultPayload = mediaSearchResultPayload as MusicSearchResultPayload
            const mediaSearchResultType = getMediaSearchResultType(musicSearchResultPayload)

            let primaryText = ""
            let secondaryText = ""
            let playMediaItemId = 0
            let selectMediaItemId = 0
            if (mediaSearchResultType === MediaSearchResultType.TRACK) {
                primaryText = `${musicSearchResultPayload.trackPayload.name} ${getTrackYearInBrackets(musicSearchResultPayload.trackPayload.year)}`
                secondaryText = musicSearchResultPayload.artistPayload.name
                playMediaItemId = musicSearchResultPayload.trackPayload.id
                selectMediaItemId = musicSearchResultPayload.albumPayload.id
            } else if (mediaSearchResultType === MediaSearchResultType.ALBUM) {
                primaryText = musicSearchResultPayload.albumPayload.name
                secondaryText = musicSearchResultPayload.artistPayload.name
                playMediaItemId = musicSearchResultPayload.albumPayload.id
                selectMediaItemId = musicSearchResultPayload.albumPayload.id
            } else if (mediaSearchResultType === MediaSearchResultType.ARTIST) {
                primaryText = musicSearchResultPayload.artistPayload.name
                playMediaItemId = musicSearchResultPayload.artistPayload.id
                selectMediaItemId = musicSearchResultPayload.artistPayload.id
            }

            return (
                <ListItem
                    key={index}
                    secondaryAction={
                        <div>
                            <IconButton
                                edge="end"
                                color="primary"
                                onClick={() => handlePlayItem(playMediaItemId, mediaSearchResultType)}>
                                <PlayArrow />
                            </IconButton>
                            <IconButton
                                edge="end"
                                color="primary"
                                onClick={() => handleAddItem(playMediaItemId, mediaSearchResultType)}>
                                <Add />
                            </IconButton>
                        </div>
                    }
                >

                    <ListItemButton
                        onClick={() => handleSelectItem(selectMediaItemId, mediaSearchResultType)}
                    >
                        <ListItemIcon>
                            {getSearchMediaItemIcon(mediaSearchResultType)}
                        </ListItemIcon>

                        <ListItemText
                            primary={primaryText}
                            secondary={secondaryText}
                        />
                    </ListItemButton>
                </ListItem>

            )
        }
    }



    const getSearchMediaItemIcon = (mediaSearchResultType: MediaSearchResultType) => {
        if (mediaSearchResultType === MediaSearchResultType.TRACK) {
            return (<Audiotrack />)
        }

        if (mediaSearchResultType === MediaSearchResultType.ALBUM) {
            return (<Album />)
        }

        if (mediaSearchResultType === MediaSearchResultType.ARTIST) {
            return (<Person />)
        }

    }

    return (
        <div>
            <h1>Search results</h1>

            <List>
                {props?.mediaSearchResultPayloads.map(function (mediaSearchResultPayload, index) {
                    return (
                        displaySearchResult(mediaSearchResultPayload, index)
                    )
                })}
            </List>

        </div>
    )
}

export default MediaSearchResults