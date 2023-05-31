import { useEffect } from "react"
import { useDispatch } from "react-redux"
import { logOut } from "./features/securitySlice"
import { removeUserPolicy } from "./features/userPolicySlice"
import { removeTokenCookie } from "./securityUtils"

const LogOut = () => {

    const dispatch = useDispatch()

    useEffect(() => {
        dispatch(
            logOut()
        )
        dispatch(
            removeUserPolicy()
        )
        removeTokenCookie()
    }, [])

    return (
        <div className="zero-top-margin">
            <h1>Logged Out</h1>
        </div>
    )
}

export default LogOut