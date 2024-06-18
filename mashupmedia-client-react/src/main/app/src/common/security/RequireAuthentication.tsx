import { useEffect } from "react"
import { useSelector } from "react-redux"
import { useAppDispatch } from "../redux/hooks"
import type { RootState } from "../redux/store"
import { redirectLogin } from "../utils/httpUtils"
import { userPolicy } from "./features/userPolicySlice"
import { securityToken } from "./securityUtils"

export function RequireAuthenication({ children }: { children: JSX.Element }) {

    const tokenPayload = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy)

    const dispatch = useAppDispatch()

    useEffect(() => {
        if (userPolicyPayload.payload) {
            return
        }

        const token = securityToken(tokenPayload)
        if (token) {
            dispatch(
                userPolicy(token)
            )
        } else {
            redirectLogin()
        }
    })

    useEffect(() => {
        if (userPolicyPayload.error) {
            redirectLogin()
        }
    }, [userPolicyPayload])

    return (
        <div>
            {userPolicyPayload.payload &&
                children
            }
        </div>
    )
}