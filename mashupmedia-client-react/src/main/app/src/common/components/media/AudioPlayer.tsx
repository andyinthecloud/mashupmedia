import { ChevronLeft, ChevronRight, Pause, PlayArrow } from "@mui/icons-material"
import { IconButton, Slider } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { RootState } from "../../redux/store"
import "./AudioPlayer.css"


export type AudioPlayerPayload = {
    playing?: boolean

}

const AudioPlayer = (payload: AudioPlayerPayload) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const [props, setProps] = useState<AudioPlayerPayload>()
    const audio = document.getElementById("#audio-player-container audio"); 


    useEffect(() => {
        setProps(payload)
    }, [payload])


    const isPlaying = (): boolean => {
        return props?.playing || false
    }

    const handlePlay = () => {
        setProps(p => ({
            ...p,
            playing: !props?.playing
        }))
    }

    return (
        <header id="audio-player-container">

            <audio className="nide">

            </audio>

            <div className="track centre">
                <span className="artist">Bob Marley</span>
                <span className="title">No woman no cry</span>
            </div>


            <div className="buttons centre">
                <IconButton color="primary">
                    <ChevronLeft fontSize="medium" />
                </IconButton>

                <IconButton color="primary" onClick={handlePlay}>
                    {isPlaying() &&
                        <Pause sx={{ fontSize: 48 }} />
                    }
                    {!isPlaying() &&
                        <PlayArrow sx={{ fontSize: 48 }} />
                    }

                </IconButton>

                <IconButton color="primary">
                    <ChevronRight fontSize="medium" />
                </IconButton>
            </div>

            <div className="duration centre">
                <div className="beginning duration-time">0:00</div>
                <Slider aria-label="Volume" min={0} max={100} defaultValue={45} />
                <div className="end duration-time">3:00</div>
            </div>

        </header>

    )

}

export default AudioPlayer