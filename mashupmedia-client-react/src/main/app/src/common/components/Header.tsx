
import "./Header.css"
import MashupBar from "./MashupBar"

// type HeaderPayload = {
//     loggedIn: boolean
// }

const Header = () => {

    // const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    // const [props, setProps] = useState<HeaderPayload>()

    // useEffect(() => {
    //     console.log("useEffect: userPolicyPayload", userPolicyPayload)
    //     setProps({
    //         loggedIn: userPolicyPayload?.username ? true : false
    //     })
    // }, [userPolicyPayload])

    return (
        <header
            id="top-bar">
            <MashupBar />
        </header>
    )
}

export default Header