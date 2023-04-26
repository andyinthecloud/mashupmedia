import { ChevronLeft, ChevronRight, ExpandLess, ExpandMore, Pause, PlayArrow, QueueMusic } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useCallback, useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate } from "react-router-dom"
import { playingTrackId } from "../../../media/music/features/playMusicSlice"
import { ImageType, albumArtImageUrl, mediaStreamUrl, playlistStreamUrl } from "../../../media/music/rest/musicCalls"
import { EncoderStatusType, MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, navigateTrack, currentTrack } from "../../../media/music/rest/playlistCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { NotificationType, addNotification } from "../../notification/notificationSlice"
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
    const playMusic = useSelector((state: RootState) => state.playMusic)


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

    const dispatch = useDispatch()

    useEffect(() => {
        if (!securityToken(userToken)) {
            return
        }
        handleNavigate({ navigatePlaylistType: NavigatePlaylistType.CURRENT })


    }, [userToken])

    useEffect(() => {
        if (!playMusic.triggerPlay) {
            return
        }

        handleNavigate({
            navigatePlaylistType: playMusic.requestPlaylistTrackId ? undefined : NavigatePlaylistType.CURRENT,
            playlistMediaItemId: playMusic.requestPlaylistTrackId
        })

    }, [playMusic.triggerPlay])

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
                <div>
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
                <div>
                    <h1 className="title">Empty playlist</h1>
                    <p><Link to={"/music/albums"} onClick={() => setExpanded(false)}>Play</Link> some music to brighten up your day.</p>
                    <p>If you have just installed Mashup Media congratulation!. Please add your music <Link to={"/configuration/libraries"} onClick={() => setExpanded(false)}>libraries</Link> to listen to your music.</p>
                </div>
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

        currentTrack(playlistId, userToken).then(response => {
            if (response.ok) {
                const securePayload = response.parsedBody

                setProps({
                    mediaToken: response.parsedBody?.mediaToken || "",
                    payload: {
                        ...props.payload,                        
                        isReadyToPlay: response.ok,
                        trackWithArtistPayload: securePayload?.payload,
                        triggerPlay: undefined
                    }
                })

                dispatch(
                    playingTrackId(securePayload?.payload.id || 0)
                )
            }
        })
    }, [])


    const handleNavigate = useCallback((navigatePlaylistPayload: NavigatePlaylistPayload) => {

        navigateTrack(navigatePlaylistPayload, userToken).then((response) => {

            if (response.ok) {

                const securePayload = response.parsedBody
                setProps({
                    ...props,
                    mediaToken: securePayload?.mediaToken || "",
                    payload: {
                        ...props.payload,
                        isReadyToPlay: true,
                        trackWithArtistPayload: securePayload?.payload,
                        triggerPlay: timestamp()
                    }
                })

                dispatch(
                    playingTrackId(securePayload?.payload.id || 0)
                )

            } else {
                setProps({
                    ...props,
                    payload: {
                        ...props.payload,
                        isReadyToPlay: false,
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
        handleNavigate({ navigatePlaylistType: NavigatePlaylistType.NEXT })
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

        console.log("trackSeconds = " + trackSeconds + ", progress = " + progress)
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

    const handleExpand = (): void => {
        setExpanded(!expanded)
    }

    const navigate = useNavigate()

    const handleAlbumClick = (albumId: number) => {
        setExpanded(false)
        navigate("/music/album/" + albumId)
    }

    const handleAudioError = (): void => {
        dispatch(
            addNotification({
                message: 'Unable to play track, please check it is correctly encoded for the web.',
                notificationType: NotificationType.ERROR
            })
        )

    }

    const encodeMessage = (encoderStatusType?: EncoderStatusType) => {
        if (!encoderStatusType || encoderStatusType == EncoderStatusType.OK) {
            return ""
        }

        let message = ""
        if (encoderStatusType == EncoderStatusType.ENODER_NOT_INSTALLED) {
            message = "Encoder is not yet configured and this track has an incompatible format for the web. Either configure the encoder through the menu under Settings -> Encode or replace this track with an mp3 file."
        } else if (encoderStatusType == EncoderStatusType.SENT_FOR_ENCODING) {
            message = "The track has been sent for encoding, it should be available soon."
        }

        return (
            <small>{message}</small>
        )
    }

    return (
        <div id="audio-player-container" >
            <audio
                ref={audioPlayer}
                className="hide"
                onEnded={() => handleNextTrack()}
                onTimeUpdate={e => handleTimeUpdate(e.currentTarget)}
                onError={handleAudioError}
            >
            </audio>

            <div className="audio-buttons">

                <div className="button-container">
                    <IconButton
                        onClick={() => handleNavigate({ navigatePlaylistType: NavigatePlaylistType.PREVIOUS })}
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
                        onClick={() => handleNavigate({ navigatePlaylistType: NavigatePlaylistType.NEXT })}
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

                            {encodeMessage(props.payload.trackWithArtistPayload?.encoderStatusType)}

                            <div
                                className="album-art"
                                style={{ backgroundImage: `url(${albumArtImageUrl(props.payload.trackWithArtistPayload?.albumPayload.id || 0, ImageType.ORIGINAL, props.mediaToken)})` }}
                                onClick={() => handleAlbumClick(props.payload.trackWithArtistPayload?.albumPayload.id || 0)}
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