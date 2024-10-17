import { useCallback, useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { SecureMediaPayload } from "../rest/secureMediaPayload"
import { MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack } from "./rest/playlistActionCalls"
import { albumArtImageUrl, ImageType } from "./rest/musicCalls"
import './TrackPlaying.css'
import { Link } from "react-router-dom"
import { Equalizer } from "@mui/icons-material"


type TrackPlayingPlayload = {
    secureMusicPlaylistTrackPayload?: SecureMediaPayload<MusicPlaylistTrackPayload>
}

const TrackPlaying = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playMusic = useSelector((state: RootState) => state.playMusic)
    const [props, setProps] = useState<TrackPlayingPlayload>()

    const handleNavigate = useCallback((navigatePlaylistPayload: NavigatePlaylistPayload) => {
        navigateTrack(navigatePlaylistPayload, userToken).then((response) => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    secureMusicPlaylistTrackPayload: response.parsedBody
                }))
            }
        })

    }, [userToken])

    useEffect(() => {
        handleNavigate({
            navigatePlaylistType: playMusic.loadPlaylistMediaItemId ? undefined : NavigatePlaylistType.CURRENT,
            playlistMediaItemId: playMusic.loadPlaylistMediaItemId,
            playlistId: playMusic.loadPlaylistId,
            loadStream: false
        })

    }, [userToken])

    useEffect(() => {
        handleNavigate({
            navigatePlaylistType: NavigatePlaylistType.CURRENT,
            playlistMediaItemId: playMusic.loadedPlaylistMediaItemId,
            playlistId: playMusic.loadPlaylistId,
            loadStream: false
        })

    }, [playMusic.loadedPlaylistMediaItemId])

    return (
        <div id="track-playing">


            <div className="title">
                <div className="track-name">
                    {props?.secureMusicPlaylistTrackPayload?.payload.trackPayload.name}
                </div>

                    <Equalizer
                        color="primary"
                    />
            </div>

            <img
                className="album-art"
                src={albumArtImageUrl(
                    props?.secureMusicPlaylistTrackPayload?.payload.albumPayload.id || 0,
                    ImageType.ORIGINAL,
                    props?.secureMusicPlaylistTrackPayload?.mediaToken || '')} />


            <div className="track-information">
                <Link
                    className="link-no-underlne artist"
                    to={"/music/artist/" + props?.secureMusicPlaylistTrackPayload?.payload.artistPayload.id}>
                    {props?.secureMusicPlaylistTrackPayload?.payload.artistPayload.name}
                </Link>
                <span className="separator">-</span>
                <Link
                    className="link-no-underlne album"
                    to={"/music/artist/" + props?.secureMusicPlaylistTrackPayload?.payload.albumPayload.id}>
                    {props?.secureMusicPlaylistTrackPayload?.payload.albumPayload.name}
                </Link>
            </div>


        </div>
    )
}

export default TrackPlaying