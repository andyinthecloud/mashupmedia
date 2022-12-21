import { ChevronLeft, ChevronRight, Pause, PlayArrow } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { mediaStreamUrl } from "../../../media/music/rest/musicCalls"
import { NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack, MusicPlaylistTrackPayload } from "../../../media/music/rest/playlistCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { RootState } from "../../redux/store"
import "./AudioPlayer.css"


export type AudioPlayerPlayload = {
    playTrigger?: number
    trackWithArtistPayload?: MusicPlaylistTrackPayload
    isReadyToPlay: boolean
    isPlaying: boolean
}

const AudioPlayer = () => {

    // https://stackoverflow.com/questions/47686345/playing-sound-in-react-js

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playTrigger = useSelector((state: RootState) => state.playMusic.trigger)

    const [props, setProps] = useState<SecureMediaPayload<AudioPlayerPlayload>>({
        mediaToken: "",
        payload: {
            isReadyToPlay: false,
            isPlaying: false
        }
    })

    const audioPlayer = useRef(new Audio())

    useEffect(() => {
        console.log("AudioPlayer: usertoken", userToken)
        handleNavigate(NavigatePlaylistType.CURRENT)
    }, [userToken])

    useEffect(() => {
        console.log("trigger play", playTrigger)
        setProps({
            ...props,
            payload: {
                ...props.payload,
                playTrigger
            }
        })
        handleNavigate(NavigatePlaylistType.CURRENT)
    }, [playTrigger])

    const renderPlayingInformation = () => {
        if (!isEmptyPlaylist()) {
            return (
                <div>
                    <span className="artist">{props.payload.trackWithArtistPayload?.artistPayload.name}</span>
                    <span className="title">{props.payload.trackWithArtistPayload?.trackPayload.name}</span>
                </div>
            )
        } else {
            return (
                <span>Empty playlist</span>
            )
        }
    }

    const isEmptyPlaylist = (): boolean => {
        return props.payload.trackWithArtistPayload?.trackPayload.name ? false : true
    }

    const disablePrevious = (): boolean => {
        if (props.payload.trackWithArtistPayload?.first) {
            return true
        }

        return isEmptyPlaylist()
    }

    const disableNext = (): boolean => {
        if (props.payload.trackWithArtistPayload?.last) {
            return true
        }

        return isEmptyPlaylist()
    }


    const handleNavigate = (navigatePlaylistType: NavigatePlaylistType): void => {

        const navigatePlaylistPayload: NavigatePlaylistPayload = {
            navigatePlaylistType
        }

        navigateTrack(navigatePlaylistPayload, userToken).then((response) => {
            if (response.ok) {
                setProps({
                    ...props,
                    mediaToken: response.parsedBody?.mediaToken || "",
                    payload: {
                        ...props.payload,
                        isReadyToPlay: response.ok,
                        trackWithArtistPayload: response.parsedBody?.payload
                    }
                })    
            }
        })
    }

    useEffect(() => {
        const trackId = props.payload.trackWithArtistPayload?.trackPayload.id
        if (trackId) {
            console.log("cueing song", trackId)
            audioPlayer.current.src = mediaStreamUrl(trackId, props.mediaToken)
            if (props.payload.isPlaying) {
                audioPlayer.current.play()
            }
        } 
    }, [props.payload.trackWithArtistPayload?.trackPayload.id])

    const handlePlay = (): void => {
        setProps({
            ...props,
            payload: {
                ...props.payload,
                isPlaying: !props.payload.isPlaying
            }
        })
    }

    useEffect(() => {
        const isPlaying = props.payload.isPlaying
        if (isPlaying) {
            audioPlayer.current.play()
        } else {
            audioPlayer.current.pause()
        }

    }, [props.payload.isPlaying])


    const trackLength = (minutes?: number, seconds?: number) => {
        const formattedMinutes = minutes || 0;
        const formattedSeconds = seconds || 0;
        const secondsValue = (formattedSeconds < 10) ? "0" + formattedSeconds : formattedSeconds;

        return (
            <span>{formattedMinutes}:{secondsValue}</span>
        )
    }

    const audioPlayerDisplayClass = (): string => {
        return props.payload.isReadyToPlay ? "" : "hide"
    }

    return (
        <div id="audio-player-container" className={audioPlayerDisplayClass()} >
            <div className="track centre">
                {renderPlayingInformation()}
            </div>
            <div className="buttons centre">
                <IconButton
                    color="primary"
                    onClick={() => handleNavigate(NavigatePlaylistType.PREVIOUS)}
                    disabled={disablePrevious()}>
                    <ChevronLeft fontSize="medium" />
                </IconButton>

                <IconButton
                    color="primary"
                    onClick={() => handlePlay()}
                    disabled={isEmptyPlaylist()}>
                    {props.payload.isPlaying
                        ? <Pause sx={{ fontSize: 48 }} />
                        : <PlayArrow sx={{ fontSize: 48 }} />
                    }
                </IconButton>

                <IconButton
                    color="primary"
                    onClick={() => handleNavigate(NavigatePlaylistType.NEXT)}
                    disabled={disableNext()}>
                    <ChevronRight fontSize="medium" />
                </IconButton>
            </div>

            <div className="duration centre">
                <div className="beginning duration-time">0:00</div>
                <Slider
                    aria-label="Volume"
                    min={0}
                    max={100}
                    defaultValue={45}
                    disabled={isEmptyPlaylist()} />
                <div className="end duration-time">{trackLength(props.payload.trackWithArtistPayload?.trackPayload.minutes, props.payload.trackWithArtistPayload?.trackPayload.seconds)} </div>
            </div>
        </div>
    )

}

export default AudioPlayer