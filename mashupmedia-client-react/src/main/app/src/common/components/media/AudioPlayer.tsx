import { ChevronLeft, ChevronRight, Pause, PlayArrow } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack, TrackWithArtistPayload } from "../../../media/music/rest/playlistCalls"
import { RootState } from "../../redux/store"
import "./AudioPlayer.css"


export type AudioPlayerPlayload = {
    playTrigger?: number
    trackWithArtistPayload?: TrackWithArtistPayload
    isReadyToPlay: boolean
}

const AudioPlayer = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playTrigger = useSelector((state: RootState) => state.playMusic.trigger)

    const [props, setProps] = useState<AudioPlayerPlayload>({ isReadyToPlay: false })

    const audioPlayer = useRef(new Audio())


    useEffect(() => {
        console.log("AudioPlayer: usertoken", userToken)
        handleNavigate(NavigatePlaylistType.CURRENT)
    }, [userToken])

    useEffect(() => {
        console.log("trigger play", playTrigger)
        setProps({
            ...props,
            playTrigger
        })
    }, [playTrigger])

    useEffect(() => {
        console.log("isReadyToPlay", props.isReadyToPlay)
    }, [props.isReadyToPlay])

    const renderPlayingInformation = () => {
        if (props.isReadyToPlay) {
            return (
                <div>
                    <span className="artist">{props.trackWithArtistPayload?.artistPayload.name}</span>
                    <span className="title">{props.trackWithArtistPayload?.trackPayload.name}</span>
                </div>
            )
        } else {
            return (
                <span>Empty playlist</span>
            )
        }
    }

    const handleNavigate = (navigatePlaylistType: NavigatePlaylistType): void => {

        const navigatePlaylistPayload: NavigatePlaylistPayload = {
            navigatePlaylistType
        }

        navigateTrack(navigatePlaylistPayload, userToken).then((response) => {
            if (response.ok) {
                setProps({
                    ...props,
                    trackWithArtistPayload: response.parsedBody?.payload,
                    isReadyToPlay: true
                })
            } else {
                setProps({
                    ...props,
                    isReadyToPlay: false
                })
            }
        })
    }

    const handlePlay = (): void => {
        audioPlayer.current.src = ''

        if (audioPlayer.current.paused) {
            audioPlayer.current.play
        } else {
            audioPlayer.current.pause
        }
    }

    const trackLength = (minutes?: number, seconds?: number) => {
        const formattedMinutes = minutes || 0;
        const formattedSeconds = seconds || 0;        
        const secondsValue = (formattedSeconds < 10) ? "0" + formattedSeconds : formattedSeconds;

        return (
            <span>{formattedMinutes}:{secondsValue}</span>
        )
    }

    return (
        <div id="audio-player-container">
            <div className="track centre">
                {renderPlayingInformation()}
            </div>
            <div className="buttons centre">
                <IconButton
                    color="primary"
                    onClick={() => handleNavigate(NavigatePlaylistType.PREVIOUS)}
                    disabled={!props.isReadyToPlay}>
                    <ChevronLeft fontSize="medium" />
                </IconButton>

                <IconButton
                    color="primary"
                    onClick={() => handlePlay()}
                    disabled={!props.isReadyToPlay}>
                    {audioPlayer.current && audioPlayer.current.paused
                        ? <PlayArrow sx={{ fontSize: 48 }} />
                        : <Pause sx={{ fontSize: 48 }} />
                    }
                </IconButton>

                <IconButton
                    color="primary"
                    onClick={() => handleNavigate(NavigatePlaylistType.NEXT)}
                    disabled={!props.isReadyToPlay}>
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
                    disabled={!props.isReadyToPlay} />
                <div className="end duration-time">{trackLength(props.trackWithArtistPayload?.trackPayload.minutes, props.trackWithArtistPayload?.trackPayload.seconds)} </div>
            </div>

        </div>

    )

}

export default AudioPlayer