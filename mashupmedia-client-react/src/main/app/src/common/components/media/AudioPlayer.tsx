import { ChevronLeft, ChevronRight, Pause, PlayArrow, ExpandLess, ExpandMore } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { albumArtImageUrl, ImageType, mediaStreamUrl, playlistStreamUrl } from "../../../media/music/rest/musicCalls"
import { MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack } from "../../../media/music/rest/playlistCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { RootState } from "../../redux/store"
import { securityToken } from "../../security/securityUtils"
import { displayDuration } from "../../utils/dateUtils"
import "./AudioPlayer.css"


type AudioPlayerPlayload = {
    trackWithArtistPayload?: MusicPlaylistTrackPayload
    isReadyToPlay: boolean
    isPlaying: boolean
    progress: number
    currentPlaylistSeconds: number
    expand: boolean
    mobile: boolean
}

const AudioPlayer = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playTrigger = useSelector((state: RootState) => state.playMusic.trigger)
    const MOBILE_MAX_WIDTH = 768

    const isMobileDisplay = (): boolean => (
        window.innerWidth <= MOBILE_MAX_WIDTH
    )

    const [props, setProps] = useState<SecureMediaPayload<AudioPlayerPlayload>>({
        mediaToken: "",
        payload: {
            isReadyToPlay: false,
            isPlaying: false,
            progress: 0,
            currentPlaylistSeconds: 0,
            expand: false,
            mobile: isMobileDisplay()
        }
    })

    const audioPlayer = useRef(new Audio())

    useEffect(() => {
        if (!securityToken(userToken)) {
            return
        }
        handleNavigate(NavigatePlaylistType.CURRENT)
    }, [userToken])

    useEffect(() => {

        if (!playTrigger) {
            return
        }

        if (props.payload.mobile) {
            setProps({
                ...props,
                payload: {
                    ...props.payload,
                    currentPlaylistSeconds: 0
                }
            })
        } else {
            handleNavigate(NavigatePlaylistType.CURRENT)
        }

    }, [playTrigger])

    const renderPlayingInformation = () => {
        if (!isEmptyPlaylist()) {
            return (
                <div>
                    <div className="title">{props.payload.trackWithArtistPayload?.trackPayload.name}</div>
                    <div className="artist">{props.payload.trackWithArtistPayload?.artistPayload.name}</div>
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

        console.log('handleNavigate')

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
                        trackWithArtistPayload: response.parsedBody?.payload,
                        currentPlaylistSeconds: props.payload.currentPlaylistSeconds + (props.payload.trackWithArtistPayload?.trackPayload.totalSeconds || 0)
                    }
                })
            } else {
                setProps({
                    ...props,
                    payload: {
                        ...props.payload,
                        trackWithArtistPayload: undefined
                    }
                })
            }
        })
    }

    useEffect(() => {

        if (!props.payload.trackWithArtistPayload?.trackPayload) {
            return
        }


        let audioUrl = ''
        const paused = audioPlayer.current.readyState > 1 ? audioPlayer.current.paused : false
        // const paused = false
        console.log("audio: trackWithArtistPayload", props.payload.trackWithArtistPayload)
        console.log("audio: paused(before)", paused)


        if (props.payload.mobile) {
            const playlistId = props.payload.trackWithArtistPayload?.playlistPayload.id || 0
            if (playlistId) {
                audioUrl = playlistStreamUrl(playlistId, props.mediaToken)
            }
        } else {
            const trackId = props.payload.trackWithArtistPayload?.trackPayload.id || 0
            if (trackId) {
                audioUrl = mediaStreamUrl(trackId, props.mediaToken)
            }
        }

        audioPlayer.current.src = audioUrl
        audioPlayer.current.load()
        if (!paused) {
            audioPlayer.current.play()
        }

    }, [props.payload.trackWithArtistPayload?.trackPayload])

    const handlePlay = (): void => {
        setProps({
            ...props,
            payload: {
                ...props.payload,
                isPlaying: !props.payload.isPlaying
            }
        })
    }

    const handleNextTrack = (): void => {
        if (props.payload.trackWithArtistPayload?.last) {
            return
        }
        handleNavigate(NavigatePlaylistType.NEXT)
    }

    useEffect(() => {
        const isPlaying = props.payload.isPlaying
        if (isPlaying) {
            audioPlayer.current.play()
        } else {
            audioPlayer.current.pause()
        }

    }, [props.payload.isPlaying])




    const handleTimeUpdate = (element: HTMLAudioElement): void => {

        if (audioPlayer.current.paused) {
            return
        }


        console.log('handleTimeUpdate: props.payload.mobile', props.payload.mobile)

        const trackProgress = element.currentTime
        setProps({
            ...props,
            payload: {
                ...props.payload,
                progress: trackProgress
            }
        })

        if (!props.payload.mobile) {
            return
        }

        const totalTrackSeconds = props.payload.trackWithArtistPayload?.trackPayload.totalSeconds
        if (!totalTrackSeconds) {
            return
        }

        if (!props.payload.mobile) {
            return
        }

        console.log('handleTimeUpdate: trackProgress', trackProgress)
        console.log('handleTimeUpdate: currentPlaylistSeconds', props.payload.currentPlaylistSeconds)

        if (trackProgress > props.payload.currentPlaylistSeconds) {
            console.log('handleTimeUpdate: next track')
            handleNavigate(NavigatePlaylistType.NEXT)
        }

    }

    const handleSlide = (value: number | number[]) => {

        const seconds = Array.isArray(value) ? value[0] : value
        const trackId = props.payload.trackWithArtistPayload?.trackPayload.id
        if (trackId) {
            audioPlayer.current.pause()
            audioPlayer.current.src = mediaStreamUrl(trackId, props.mediaToken, seconds)
            if (props.payload.isPlaying) {
                audioPlayer.current.play()
            }
        }

        setProps({
            ...props,
            payload: {
                ...props.payload,
                progress: seconds
            }
        })
    }

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

    const handleExpand = (): void => {
        setProps({
            ...props,
            payload: {
                ...props.payload,
                expand: !props.payload.expand
            }
        })
    }

    return (
        <div id="audio-player-container" className={audioPlayerDisplayClass()} >
            <audio
                ref={audioPlayer}
                className="hide"
                onEnded={() => handleNextTrack()}
                onTimeUpdate={e => handleTimeUpdate(e.currentTarget)}
            >
            </audio>


            <div className="audio-buttons">

                <div className="button-container">
                    <IconButton
                        onClick={() => handleNavigate(NavigatePlaylistType.PREVIOUS)}
                        disabled={disablePrevious()}>
                        <ChevronLeft fontSize="medium" />
                    </IconButton>

                    <IconButton
                        onClick={() => handlePlay()}
                        disabled={isEmptyPlaylist()}
                        className="play-button">
                        {props.payload.isPlaying
                            ? <Pause sx={{ fontSize: 48 }} />
                            : <PlayArrow sx={{ fontSize: 48 }} />
                        }
                    </IconButton>

                    <IconButton
                        onClick={() => handleNavigate(NavigatePlaylistType.NEXT)}
                        disabled={disableNext()}>
                        <ChevronRight fontSize="medium" />
                    </IconButton>
                </div>



                <div className="expand-more">
                    <IconButton
                        onClick={handleExpand}>
                        {!props.payload.expand &&
                            <ExpandMore />
                        }
                        {props.payload.expand &&
                            <ExpandLess />
                        }
                    </IconButton>

                </div>

            </div>

            {props.payload.expand &&

                <div className="expand">

                    <div className="container">

                        {!props.payload.mobile &&
                            <div className="duration centre">
                                <div className="beginning duration-time">{displayDuration(props.payload.progress)}</div>
                                <Slider
                                    aria-label="Volume"
                                    min={0}
                                    max={props.payload.trackWithArtistPayload?.trackPayload.totalSeconds}
                                    value={props.payload.progress}
                                    disabled={isEmptyPlaylist()}
                                    onChangeCommitted={(event, value) => handleSlide(value)} />
                                <div className="end duration-time">{trackLength(props.payload.trackWithArtistPayload?.trackPayload.minutes, props.payload.trackWithArtistPayload?.trackPayload.seconds)} </div>
                            </div>
                        }

                        <div className="track-information">
                            <div className="track">
                                {renderPlayingInformation()}
                            </div>
                            <div
                                className="album-art"
                                style={{ backgroundImage: `url(${albumArtImageUrl(props.payload.trackWithArtistPayload?.albumPayload.id || 0, ImageType.ORIGINAL, props.mediaToken)})` }}
                            />
                        </div>



                    </div>

                </div>
            }



        </div>
    )

}

export default AudioPlayer