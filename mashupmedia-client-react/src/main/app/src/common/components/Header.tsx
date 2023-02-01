import "./Header.css"
import MashupBar from "./MashupBar"
import AudioPlayer from "./media/AudioPlayer"

export type HeaderPayload = {
    loggedIn: boolean
}

const Header = () => {
    
    return (
        <header
            id="top-bar">
            <MashupBar />
            <AudioPlayer
            />
        </header>
    )
}

export default Header