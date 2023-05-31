
import { useSelector } from "react-redux"
import "./Header.css"
import MashupBar from "./MashupBar"
import AudioPlayer from "./media/AudioPlayer"
import { RootState } from "../redux/store"
import { useState, useEffect } from "react"

type HeaderPayload = {
    loggedIn: boolean
}

const Header = () => {

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const [props, setProps] = useState<HeaderPayload>()

    useEffect(() => {
        console.log("useEffect: userPolicyPayload", userPolicyPayload)
        setProps({
            loggedIn: userPolicyPayload?.username ? true : false
        })
    }, [userPolicyPayload])

    return (
        <header
            id="top-bar">
            <MashupBar />

            {props?.loggedIn &&
                <AudioPlayer />
            }
        </header>
    )
}

export default Header