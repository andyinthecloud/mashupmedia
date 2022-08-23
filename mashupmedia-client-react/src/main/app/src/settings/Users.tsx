import { AdminPanelSettings, Person } from "@mui/icons-material"
import { Button, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../redux/store"
import { HttpStatus, redirectLogin } from "../utils/httpUtils"
import { UserPayload, users } from "./backend/userCalls"

const Users = () => {

    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    const [props, setProps] = useState<UserPayload[]>([])


    useEffect(() => {
        users(userToken)
            .then((response) => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)
                }
            })
            .catch(() => redirectLogin(HttpStatus.FORBIDDEN))

    }, [userToken])

    const userIcon = (userPayload: UserPayload) => {
        return userPayload.administrator
            ? <AdminPanelSettings />
            : <Person />
    }


    const navigate = useNavigate()
    function handleCancel(): void {
        navigate('/')
    }


    function handleNewUser(): void {
        navigate('/settings/new-account')
    }

    function handleClickUser(username: string): void {
        navigate('/settings/user-account/' + encodeURIComponent(username))
    }


    return (
        <form>

            <h1>Users</h1>

            <List>
                {props.map(function (userPayload) {
                    return (
                        <ListItem key={userPayload.username} onClick={() => handleClickUser(userPayload.username)}>
                            <ListItemButton>
                                <ListItemIcon>
                                    {userIcon(userPayload)}
                                </ListItemIcon>
                                <ListItemText>{userPayload.name}</ListItemText>
                            </ListItemButton>
                        </ListItem>
                    )
                })}
            </List>

            
            <div className="new-line right">
                <Button variant="contained" color="secondary"  type="button" onClick={handleCancel}>
                    Cancel
                </Button>
                <Button variant="contained" color="primary" type="submit" onClick={handleNewUser}>
                    New user
                </Button>
            </div>


        </form>
    )

}

export default Users