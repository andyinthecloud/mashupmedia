
import "./Header.css"
import MashupBar from "./MashupBar"
import AudioPlayer from "./media/AudioPlayer"

const Header = () => {


    return (
        <header
            id="top-bar">
            <MashupBar />
            <AudioPlayer/>
        </header>
    )
}

export default Header