import { ChevronLeft, ChevronRight, Favorite, FavoriteBorder, MusicNote, Pause, PlayArrow, QueueMusic } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { t } from "i18next"
import { useCallback, useEffect, useRef, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link } from "react-router-dom"
import { loadedTrack } from "../../../media/music/features/playMusicSlice"
import { mediaStreamUrl, playlistStreamUrl } from "../../../media/music/rest/musicCalls"
import { MusicPlaylistTrackPayload, NavigatePlaylistPayload, NavigatePlaylistType, currentTrack, navigateTrack } from "../../../media/music/rest/playlistActionCalls"
import { SecureMediaPayload } from "../../../media/rest/secureMediaPayload"
import { voteMediaItem } from "../../../media/rest/socialCalls"
import { NotificationType, addNotification } from "../../notification/notificationSlice"
import { RootState } from "../../redux/store"
import { displayDuration } from "../../utils/dateUtils"
import { timestamp } from "../../utils/httpUtils"
import "./AudioPlayer.css"


type AudioPlayerPlayload = {
    musicPlaylistTrackPayload?: MusicPlaylistTrackPayload
    isReadyToPlay: boolean
    loadStreamTrigger?: number
    votedUp: boolean
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
            votedUp: false
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


    const isEmptyPlaylist = (): boolean => {
        return props.payload.musicPlaylistTrackPayload?.trackPayload.name ? false : true
    }

    const disablePrevious = (): boolean => {
        if (props.payload.musicPlaylistTrackPayload?.first) {
            return true
        }

        return isEmptyPlaylist()
    }

    const disableNext = (): boolean => {
        if (props.payload.musicPlaylistTrackPayload?.last) {
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
                        musicPlaylistTrackPayload: securePayload?.payload,
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
                        musicPlaylistTrackPayload: securePayload?.payload,
                        loadStreamTrigger: navigatePlaylistPayload.loadStream ? timestamp() : undefined,
                        votedUp: securePayload?.payload.trackPayload.votedUp || false
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
                        musicPlaylistTrackPayload: undefined
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

        if (!props.payload.musicPlaylistTrackPayload?.trackPayload) {
            return
        }

        let audioUrl = ''
        const audioWasPlaying = audioPlayer.current.readyState > 1 ? playing : false

        if (mobileDisplay) {
            const playlistId = props.payload.musicPlaylistTrackPayload?.playlistPayload.id || 0
            if (playlistId) {
                audioUrl = playlistStreamUrl(playlistId, props.mediaToken)
            }
        } else {
            const trackId = props.payload.musicPlaylistTrackPayload?.trackPayload.id || 0
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
        if (props.payload.musicPlaylistTrackPayload?.last) {
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

        const trackSeconds = props?.payload.musicPlaylistTrackPayload?.trackPayload.totalSeconds || 0

        console.log("trackSeconds = " + trackSeconds + ", progress = " + progress)
        if ((progress - playlistOffset) > trackSeconds) {
            const playlistId = props?.payload.musicPlaylistTrackPayload?.playlistPayload.id
            if (playlistId) {
                setPlaylistOffset(playlistOffset + trackSeconds)
                displayNextTrack(playlistId)
            }

        }
    }

    const handleSlide = (value: number | number[]) => {

        const paused = audioPlayer.current.paused
        const seconds = Array.isArray(value) ? value[0] : value
        const trackId = props.payload.musicPlaylistTrackPayload?.trackPayload.id
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

    const handleAudioError = (): void => {
        dispatch(
            addNotification({
                message: t("audioPlayer.errorStreaming"),
                notificationType: NotificationType.ERROR
            })
        )

    }

    function isVotingDisabled(): boolean {
        return props.payload.musicPlaylistTrackPayload?.trackPayload.disableVotes || false
    }

    function likeTrack(mediaItemId: number): void {

        if (!mediaItemId) {
            return
        }


        voteMediaItem({
            mediaItemId
        }, userToken).then(response => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    payload: {
                        ...p.payload,
                        votedUp: !props.payload.votedUp
                    }
                }))
            }
        })

    }

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
                        max={props.payload.musicPlaylistTrackPayload?.trackPayload.totalSeconds}
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
                        <div className="end duration-time">{trackLength(props.payload.musicPlaylistTrackPayload?.trackPayload.minutes, props.payload.musicPlaylistTrackPayload?.trackPayload.seconds)} </div>
                    </div>

                </div>
            }

            {!isEmptyPlaylist() &&
                <div className="playing">
                    <div className="meta">
                        <div className="track">{props.payload.musicPlaylistTrackPayload?.trackPayload.name}</div>
                        <div className="artist">{props.payload.musicPlaylistTrackPayload?.artistPayload.name}</div>
                    </div>

                    <div className={"like" + (isVotingDisabled() ? " disabled" : "")}>
                        <IconButton
                            disabled={isVotingDisabled()}
                            onClick={() => likeTrack(props.payload.musicPlaylistTrackPayload?.trackPayload.id || 0)}
                        >
                            {props.payload.votedUp &&
                                <Favorite
                                    color="secondary"
                                    sx={{
                                        color: "#ff0066"
                                    }}
                                />
                            }

                            {!props.payload.votedUp &&
                                <FavoriteBorder
                                    color="secondary"
                                    sx={{
                                        color: "#ff0066"
                                    }}
                                />
                            }


                        </IconButton>
                    </div>

                </div>
            }

            {!isEmptyPlaylist() &&
                <div className="audio-buttons">
                    <Link
                        to={"/playlists/music/playing"}
                    >
                        <MusicNote
                            color="secondary"
                        />
                    </Link>

                    <div className="button-container">
                        <IconButton
                            onClick={() => handleNavigate({
                                navigatePlaylistType: NavigatePlaylistType.PREVIOUS,
                                playlistId: props.payload.musicPlaylistTrackPayload?.playlistPayload.id,
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
                                playlistId: props.payload.musicPlaylistTrackPayload?.playlistPayload.id,
                                loadStream: true
                            })}
                            disabled={disableNext()}>
                            <ChevronRight
                                color="primary"
                                fontSize="medium" />
                        </IconButton>
                    </div>

                    <Link
                        to={"/playlists/music/" + props.payload.musicPlaylistTrackPayload?.playlistPayload.id}
                    >
                        <QueueMusic
                            color="secondary"
                            fontSize="large"
                        />
                    </Link>
                </div>
            }
        </div>
    )

}

export default AudioPlayer