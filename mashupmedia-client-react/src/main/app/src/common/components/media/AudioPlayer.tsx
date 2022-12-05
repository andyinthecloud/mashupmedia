import { ChevronLeft, ChevronRight, Pause, PlayArrow } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { PlayMusicPayload } from "../../../media/music/features/playMusicSlice"
import { RootState } from "../../redux/store"
import "./AudioPlayer.css"


const AudioPlayer = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const playTrigger = useSelector((state: RootState) => state.playMusic.trigger)

    const [props, setProps] = useState<PlayMusicPayload>()
    const audioPlayer = useRef(new Audio())

    useEffect(() => {
        console.log("trigger play", playTrigger)
        setProps({ trigger: playTrigger })
    }, [playTrigger])


    const isPlaylistEmpty = (): boolean => {
        return true;
    }



    const renderPlayingInformation = () => {
        if (isPlaylistEmpty()) {
            return (
                <span>Empty playlist</span>
            )
        } else {
            return (
                <div>
                    <span className="artist">Bob Marley</span>
                    <span className="title">No woman no cry</span>
                </div>
            )
        }
    }


    return (
        <div id="audio-player-container">
            <div className="track centre">
                {renderPlayingInformation()}
            </div>
            <div className="buttons centre">
                <IconButton
                    color="primary"
                    disabled={isPlaylistEmpty()}>
                    <ChevronLeft fontSize="medium" />
                </IconButton>

                <IconButton
                    color="primary"
                    disabled={isPlaylistEmpty()}>

                    {audioPlayer.current && audioPlayer.current.paused
                        ? <PlayArrow sx={{ fontSize: 48 }} />
                        : <Pause sx={{ fontSize: 48 }} />
                    }

                </IconButton>

                <IconButton
                    color="primary"
                    disabled={isPlaylistEmpty()}>
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
                    disabled={isPlaylistEmpty()} />
                <div className="end duration-time">3:00</div>
            </div>

        </div>

    )

}

export default AudioPlayer