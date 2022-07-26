import { FC, Props } from "react"
import { useSelector } from "react-redux"
import { Navigate, useLocation } from "react-router-dom"
import type { RootState } from "../redux/store"
import { securityToken } from "./securityUtils"


// const RequireAuthenication:FC =  ({ children }) => {

//     const userPayload = useSelector((state: RootState) => state.loggedInUser.payload)
//     let location = useLocation()
//     // if (isLoggedIn(userPayload)) {
//     //     return children
//     // }

//     return (
        
//         {
//             if (isLoggedIn(userPayload)) {
//                 return children
//             } else {
//                 return <Navigate to="/login" state={{ from: location }} replace />
//             }

//         }


//         <Navigate to="/login" state={{ from: location }} replace />
//     )
// }

// export default RequireAuthenication


export function RequireAuthenication({ children }: { children: JSX.Element }): any {
    const userPayload = useSelector((state: RootState) => state.loggedInUser.payload)
    const location = useLocation()

    if (securityToken(userPayload?.token)) {
        return children
    } else {
        <Navigate to="/login" state={{ from: location }} replace />
    }
}
