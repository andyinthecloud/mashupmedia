import { ChevronLeft, ChevronRight, ExpandLess, ExpandMore, Pause, PlayArrow, QueueMusic } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useCallback, useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { Link } from "react-router-dom"
import { albumArtImageUrl, ImageType, mediaStreamUrl, playlistStreamUrl } from "../../../media/music/rest/musicCalls"
import { MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack, trackProgress } from "../../../media/music/rest/playlistCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { RootState } from "../../redux/store"
import { securityToken } from "../../security/securityUtils"
import { displayDuration } from "../../utils/dateUtils"
import { timestamp } from "../../utils/httpUtils"
import "./AudioPlayer.css"


type AudioPlayerPlayload = {
    trackWithArtistPayload?: MusicPlaylistTrackPayload
    isReadyToPlay: boolean
    triggerPlay?: number
}

const AudioPlayer = () => {

    const MOBILE_MAX_WIDTH = 768

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playTrigger = useSelector((state: RootState) => state.playMusic.trigger)


    const isMobileDisplay = (): boolean => (
        window.innerWidth <= MOBILE_MAX_WIDTH
    )

    const [props, setProps] = useState<SecureMediaPayload<AudioPlayerPlayload>>({
        mediaToken: "",
        payload: {
            isReadyToPlay: false,
        }
    })

    const [progress, setProgress] = useState<number>(0)
    const [mobileDisplay, setMobileDisplay] = useState<boolean>(isMobileDisplay())
    const [expanded, setExpanded] = useState<boolean>(false)
    const [playing, setPlaying] = useState<boolean>(false)
    const [playlistOffset, setPlaylistOffset] = useState<number>(0)

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

        handleNavigate(NavigatePlaylistType.CURRENT)

    }, [playTrigger])

    useEffect(() => {

        const handleResize = () => {
            setMobileDisplay(isMobileDisplay)
        }

        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };

    }, []);

    const renderPlayingInformation = () => {
        if (!isEmptyPlaylist()) {
            return (
                <div >
                    <div style={{ float: "right" }}>
                        <Link
                            to={"/music/music-playlist/" + props.payload.trackWithArtistPayload?.playlistPayload.id}
                            onClick={() => setExpanded(false)}
                            className="link-no-underlne"
                        >
                            <QueueMusic
                                color="primary"
                                fontSize="large"
                            />
                        </Link>
                    </div>
                    <div className="title">{props.payload.trackWithArtistPayload?.trackPayload.name}</div>
                    <div className="artist">
                        <Link
                            to={"/music/artist/" + props.payload.trackWithArtistPayload?.artistPayload.id}
                            onClick={() => setExpanded(false)}
                            className="link-no-underlne"
                        >
                            {props.payload.trackWithArtistPayload?.artistPayload.name}
                        </Link>
                    </div>

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


    const displayNextTrack = useCallback((playlistId: number, progress: number) => {

        trackProgress(playlistId, progress, userToken).then(response => {
            if (response.ok) {
                setProps({
                    mediaToken: response.parsedBody?.mediaToken || "",
                    payload: {
                        ...props.payload,
                        isReadyToPlay: response.ok,
                        trackWithArtistPayload: response.parsedBody?.payload,
                        triggerPlay: undefined
                    }
                })
            }
        })
    }, [])


    const handleNavigate = useCallback((navigatePlaylistType: NavigatePlaylistType) => {


        console.log('handleNavigate', navigatePlaylistType)

        const navigatePlaylistPayload: NavigatePlaylistPayload = {
            navigatePlaylistType
        }

        navigateTrack(navigatePlaylistPayload, userToken).then((response) => {
            if (response.ok) {
                const securePayload = response.parsedBody
                setProps({
                    ...props,
                    mediaToken: securePayload?.mediaToken || "",
                    payload: {
                        ...props.payload,
                        isReadyToPlay: response.ok,
                        trackWithArtistPayload: securePayload?.payload,
                        triggerPlay: timestamp()
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

            setProgress(0)
        })

    }, [userToken])

    useEffect(() => {

        if (!props.payload.triggerPlay) {
            return
        }

        if (!props.payload.trackWithArtistPayload?.trackPayload) {
            return
        }

        let audioUrl = ''
        const audioWasPlaying = audioPlayer.current.readyState > 1 ? playing : false

        if (mobileDisplay) {
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

        if (!audioUrl) {
            return
        }

        setPlaylistOffset(0)
        audioPlayer.current.src = audioUrl
        audioPlayer.current.load()
        if (audioWasPlaying) {
            audioPlayer.current.play()
        }

    }, [props.payload.triggerPlay])

    const handlePlay = (): void => {
        setPlaying(!playing)
    }

    useEffect(() => {
        if (playing) {
            audioPlayer.current.play()
        } else {
            audioPlayer.current.pause()
        }

    }, [playing])

    const handleNextTrack = (): void => {
        if (props.payload.trackWithArtistPayload?.last) {
            return
        }
        handleNavigate(NavigatePlaylistType.NEXT)
    }

    const handleTimeUpdate = (element: HTMLAudioElement): void => {

        if (audioPlayer.current.paused) {
            return
        }

        const progress = element.currentTime
        setProgress(progress)

        if (!mobileDisplay) {
            return
        }
        const trackSeconds = props?.payload.trackWithArtistPayload?.trackPayload.totalSeconds || 0

        if ((progress - playlistOffset) > trackSeconds) {
            const playlistId = props?.payload.trackWithArtistPayload?.playlistPayload.id
            if (playlistId) {
                setPlaylistOffset(playlistOffset + trackSeconds)            
                displayNextTrack(playlistId, progress)    
            }

        }
    }

    const handleSlide = (value: number | number[]) => {

        const paused = audioPlayer.current.paused
        const seconds = Array.isArray(value) ? value[0] : value
        const trackId = props.payload.trackWithArtistPayload?.trackPayload.id
        if (trackId) {
            audioPlayer.current.pause()
            audioPlayer.current.src = mediaStreamUrl(trackId, props.mediaToken, seconds)
            if (!paused) {
                audioPlayer.current.play()
            }
        }

        setProgress(seconds)
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
        setExpanded(!expanded)
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
                        disabled={disablePrevious()}
                    >
                        <ChevronLeft
                            color="primary"
                            fontSize="medium" />
                    </IconButton>

                    <IconButton
                        onClick={() => handlePlay()}
                        disabled={isEmptyPlaylist()}
                        className="play-button"
                    >
                        {playing
                            ? <Pause sx={{ fontSize: 48 }} color="primary" />
                            : <PlayArrow sx={{ fontSize: 48 }} color="primary" />
                        }
                    </IconButton>

                    <IconButton
                        onClick={() => handleNavigate(NavigatePlaylistType.NEXT)}
                        disabled={disableNext()}>
                        <ChevronRight
                            color="primary"
                            fontSize="medium" />
                    </IconButton>
                </div>



                <div className="expand-more">
                    <IconButton
                        onClick={handleExpand}>
                        {!expanded &&
                            <ExpandMore
                                fontSize="large"
                                color="primary" />
                        }
                        {expanded &&
                            <ExpandLess
                                fontSize="large"
                                color="primary" />
                        }
                    </IconButton>

                </div>

            </div>

            {expanded &&

                <div className="expand">

                    <div className="container">

                        {!mobileDisplay &&
                            <div className="duration centre">
                                <div className="beginning duration-time">{displayDuration(progress)}</div>
                                <Slider
                                    aria-label="Volume"
                                    min={0}
                                    max={props.payload.trackWithArtistPayload?.trackPayload.totalSeconds}
                                    value={progress}
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

                        <div className="bottom">
                            <IconButton
                                color="primary"
                                onClick={handleExpand}>
                                <ExpandLess fontSize="large" />
                            </IconButton>
                        </div>
                    </div>

                </div>
            }



        </div>
    )

}

export default AudioPlayer