import { ChevronLeft, ChevronRight, FavoriteBorder, MusicNote, Pause, PlayArrow, QueueMusic } from "@mui/icons-material"
import { IconButton, Slider, Tooltip } from "@mui/material"
import { useCallback, useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link } from "react-router-dom"
import { loadedTrack } from "../../../media/music/features/playMusicSlice"
import { mediaStreamUrl, playlistStreamUrl } from "../../../media/music/rest/musicCalls"
import { MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, currentTrack, navigateTrack } from "../../../media/music/rest/playlistActionCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { NotificationType, addNotification } from "../../notification/notificationSlice"
import { RootState } from "../../redux/store"
import { displayDuration } from "../../utils/dateUtils"
import { timestamp } from "../../utils/httpUtils"
import "./AudioPlayer.css"


type AudioPlayerPlayload = {
    trackWithArtistPayload?: MusicPlaylistTrackPayload
    isReadyToPlay: boolean
    loadStreamTrigger?: number
    loggedIn?: boolean
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
    // const [expanded, setExpanded] = useState<boolean>(false)
    const [playing, setPlaying] = useState<boolean>(false)
    const [playlistOffset, setPlaylistOffset] = useState<number>(0)

    const audioPlayer = useRef(new Audio())

    const dispatch = useDispatch()

    useEffect(() => {
        handleNavigate({
            navigatePlaylistType: playMusic.loadPlaylistMediaItemId ? undefined : NavigatePlaylistType.CURRENT,
            playlistMediaItemId: playMusic.loadPlaylistMediaItemId,
            playlistId: playMusic.loadPlaylistId,
            loadStream: true
        })

    }, [userToken])

    useEffect(() => {
        if (!playMusic.triggerPlay) {
            return
        }

        handleNavigate({
            navigatePlaylistType: playMusic.loadPlaylistMediaItemId ? undefined : NavigatePlaylistType.CURRENT,
            playlistMediaItemId: playMusic.loadPlaylistMediaItemId,
            playlistId: playMusic.loadPlaylistId,
            loadStream: true
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

    // const renderPlayingInformation = () => {
    //     if (!isEmptyPlaylist()) {
    //         return (
    //             <div>
    //                 <div style={{ float: "right" }}>
    //                     <Link
    //                         to={"/playlists/music/" + props.payload.trackWithArtistPayload?.playlistPayload.id}
    //                         onClick={() => setExpanded(false)}
    //                         className="link-no-underlne"
    //                     >
    //                         <QueueMusic
    //                             color="primary"
    //                             fontSize="large"
    //                         />
    //                     </Link>
    //                 </div>
    //                 <div className="title">{props.payload.trackWithArtistPayload?.trackPayload.name}</div>
    //                 <div className="album">
    //                     <Link
    //                         to={"/music/album/" + props.payload.trackWithArtistPayload?.albumPayload.id}
    //                         onClick={() => setExpanded(false)}
    //                         className="link-no-underlne"
    //                     >
    //                         {props.payload.trackWithArtistPayload?.albumPayload.name}
    //                     </Link>
    //                 </div>

    //                 <div className="artist">
    //                     <Link
    //                         to={"/music/artist/" + props.payload.trackWithArtistPayload?.artistPayload.id}
    //                         onClick={() => setExpanded(false)}
    //                         className="link-no-underlne"
    //                     >
    //                         {props.payload.trackWithArtistPayload?.artistPayload.name}
    //                     </Link>
    //                 </div>

    //             </div>
    //         )
    //     } else {
    //         return (
    //             <div>
    //                 <h1 className="title">Empty playlist</h1>
    //                 <p><Link to={"/music/albums"} onClick={() => setExpanded(false)}>Play</Link> some music to brighten up your day.</p>
    //                 <p>If you have just installed Mashup Media congratulation!. Please add your music <Link to={"/configuration/libraries"} onClick={() => setExpanded(false)}>libraries</Link> to listen to your music.</p>
    //             </div>
    //         )
    //     }
    // }

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

    const displayNextTrack = useCallback((playlistId: number) => {

        currentTrack(playlistId, userToken).then(response => {
            if (response.ok) {
                const securePayload = response.parsedBody

                setProps({
                    mediaToken: response.parsedBody?.mediaToken || "",
                    payload: {
                        ...props.payload,
                        isReadyToPlay: response.ok,
                        trackWithArtistPayload: securePayload?.payload,
                        loadStreamTrigger: undefined
                    }
                })

                handleNavigate({
                    playlistId: securePayload?.payload.playlistPayload.id,
                    playlistMediaItemId: securePayload?.payload.id,
                    loadStream: false
                })
            }
        })
    }, [])


    const handleNavigate = useCallback((navigatePlaylistPayload: NavigatePlaylistPayload) => {

        console.log("handleNavigate", navigatePlaylistPayload)

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
                        loadStreamTrigger: navigatePlaylistPayload.loadStream ? timestamp() : undefined
                    }
                })

                if (securePayload?.payload.id) {
                    dispatch(
                        loadedTrack(securePayload.payload.id)
                    )
                }

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

        if (!props.payload.loadStreamTrigger) {
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

        console.log("audioUrl", audioUrl)

        if (!audioUrl) {
            return
        }

        setPlaylistOffset(0)
        audioPlayer.current.src = audioUrl
        audioPlayer.current.load()
        if (audioWasPlaying) {
            audioPlayer.current.play()
        }

    }, [props.payload.loadStreamTrigger])

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
        handleNavigate({
            navigatePlaylistType: NavigatePlaylistType.NEXT,
            loadStream: true
        })
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
                displayNextTrack(playlistId)
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

    // const handleExpand = (): void => {
    //     setExpanded(!expanded)
    // }

    const handleAudioError = (): void => {
        dispatch(
            addNotification({
                message: 'Unable to play track, please check it is correctly encoded for the web.',
                notificationType: NotificationType.ERROR
            })
        )

    }

    // const encodeMessage = (encoderStatusType?: EncoderStatusType) => {
    //     if (!encoderStatusType || encoderStatusType == EncoderStatusType.OK) {
    //         return ""
    //     }

    //     let message = ""
    //     if (encoderStatusType == EncoderStatusType.ENODER_NOT_INSTALLED) {
    //         message = "Encoder is not yet configured and this track has an incompatible format for the web. Either configure the encoder through the menu under Settings -> Encode or replace this track with an mp3 file."
    //     } else if (encoderStatusType == EncoderStatusType.SENT_FOR_ENCODING) {
    //         message = "The track has been sent for encoding, it should be available soon."
    //     }

    //     return (
    //         <small>{message}</small>
    //     )
    // }

    return (
        <div id="audio-player">
            <audio
                ref={audioPlayer}
                className="hide"
                onEnded={() => handleNextTrack()}
                onTimeUpdate={e => handleTimeUpdate(e.currentTarget)}
                onError={handleAudioError}
            >
            </audio>


            {!isEmptyPlaylist() && !mobileDisplay &&
                <div className="track-progress">

                    <Slider
                        aria-label="Volume"
                        min={0}
                        max={props.payload.trackWithArtistPayload?.trackPayload.totalSeconds}
                        value={progress}
                        disabled={isEmptyPlaylist()}
                        onChangeCommitted={(event, value) => handleSlide(value)}
                        color="secondary"
                        sx={{
                            '& .MuiSlider-thumb': {
                                height: "15px",
                                width: "15px"
                            },
                          }}
                    />

                    <div className="track-time">
                        <div className="beginning duration-time">{displayDuration(progress)}</div>
                        <div className="end duration-time">{trackLength(props.payload.trackWithArtistPayload?.trackPayload.minutes, props.payload.trackWithArtistPayload?.trackPayload.seconds)} </div>
                    </div>

                </div>
            }


            {!isEmptyPlaylist() &&
                <div className="playing">
                    <div className="meta">
                        <div className="track">{props.payload.trackWithArtistPayload?.trackPayload.name}</div>
                        <div className="artist">{props.payload.trackWithArtistPayload?.artistPayload.name}</div>
                    </div>

                    <div className="like">
                        <IconButton>
                            <FavoriteBorder
                                color="secondary"
                                sx={{
                                    color: "#ff0066"
                                }}
                            />
                        </IconButton>
                    </div>

                </div>
            }

            {!isEmptyPlaylist() &&
                <div className="audio-buttons">
                    <Link
                        to={"/playlists/music/playing"}
                    >
                        <Tooltip title="bum">
                            <MusicNote
                                color="secondary"
                            />
                        </Tooltip>
                    </Link>

                    <div className="button-container">
                        <IconButton
                            onClick={() => handleNavigate({
                                navigatePlaylistType: NavigatePlaylistType.PREVIOUS,
                                playlistId: props.payload.trackWithArtistPayload?.playlistPayload.id,
                                loadStream: true
                            })}
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
                            onClick={() => handleNavigate({
                                navigatePlaylistType: NavigatePlaylistType.NEXT,
                                playlistId: props.payload.trackWithArtistPayload?.playlistPayload.id,
                                loadStream: true
                            })}
                            disabled={disableNext()}>
                            <ChevronRight
                                color="primary"
                                fontSize="medium" />
                        </IconButton>
                    </div>



                    <Link
                        to={"/playlists/music/" + props.payload.trackWithArtistPayload?.playlistPayload.id}
                    >
                        <QueueMusic
                            color="secondary"
                            fontSize="large"
                        />
                    </Link>


                    {/* <div className="expand-more">
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

                </div> */}
                </div>
            }

            {/* {expanded &&

                <div className="expand">

                    <div
                        className="container album-art"
                        style={{ backgroundImage: `url(${albumArtImageUrl(props.payload.trackWithArtistPayload?.albumPayload.id || 0, ImageType.ORIGINAL, props.mediaToken)})` }}
                    >

                        {!mobileDisplay &&
                            <div className="duration centre blur-background">
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

                        <div className="track-information blur-background">
                            <div className="track">
                                {renderPlayingInformation()}
                            </div>
                            {encodeMessage(props.payload.trackWithArtistPayload?.encoderStatusType)}
                        </div>

                        <div className="bottom blur-background">
                            <IconButton
                                color="primary"
                                onClick={handleExpand}>
                                <ExpandLess fontSize="large" />
                            </IconButton>
                        </div>
                    </div>

                </div>
            } */}



        </div>
    )

}

export default AudioPlayer