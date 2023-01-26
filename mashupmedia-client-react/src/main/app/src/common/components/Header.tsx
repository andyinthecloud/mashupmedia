import { useSelector } from "react-redux"
import MashupBar from "./MashupBar"
import AudioPlayer from "./media/AudioPlayer"
import { RootState } from "../redux/store"
import { securityToken } from "../security/securityUtils"
import  "./Header.css"

const Header = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const hasUserToken = (): boolean => (
        securityToken(userToken) ? true : false
    )

    return (
        <header id="top-bar">
            <MashupBar />
            {hasUserToken() && <AudioPlayer/>}
        </header>
    )
}

export default Header