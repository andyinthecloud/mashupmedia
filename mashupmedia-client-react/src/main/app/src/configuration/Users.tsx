import { AdminPanelSettings, Person, VerifiedTwoTone, VerifiedUser } from "@mui/icons-material"
import { Avatar, Button, Icon, IconButton, List, ListItem, ListItemAvatar, ListItemButton, ListItemIcon, ListItemText, Tooltip } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useLocation, useNavigate } from "react-router-dom"
import { RootState } from "../common/redux/store"
import { HttpStatus, redirectLogin } from "../common/utils/httpUtils"
import { UserPayload, getUsers } from "./backend/userCalls"

const Users = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const [props, setProps] = useState<UserPayload[]>([])

    useEffect(() => {
        loadUsers()
    }, [])

    const {state} = useLocation()

    useEffect(() => {
        console.log('users useLocation')
        loadUsers()
    }, [state])

    const loadUsers = (): void => {
        getUsers(userToken)
            .then((response) => {
                if (response.parsedBody !== undefined) {
                    setProps(response.parsedBody)
                }
            })
            .catch(() =>
                redirectLogin(HttpStatus.FORBIDDEN)
            )
    } 

    const navigate = useNavigate()
    function handleCancel(): void {
        navigate('/')
    }


    function handleNewUser(): void {
        navigate('/create-user')
    }

    function handleClickUser(username: string): void {
        navigate('/configuration/user-account/' + encodeURIComponent(username))
    }


    return (
        <form>

            <h1>Users</h1>

            <List>
                {props.map(function (userPayload) {
                    return (
                        <ListItem
                            key={userPayload.username}
                            onClick={() => handleClickUser(userPayload.username)}
                            secondaryAction={
                                userPayload.administrator && (
                                    <AdminPanelSettings />
                                )
                            }

                        >
                            <ListItemButton>
                                <ListItemAvatar>
                                    <Avatar>
                                        {userPayload.validated &&
                                            <Tooltip title="Validated">
                                                <VerifiedUser />
                                            </Tooltip>
                                        }
                                        {!userPayload.validated &&
                                            <Tooltip title="Not yet validated">
                                                <Person />
                                            </Tooltip>
                                        }
                                    </Avatar>
                                </ListItemAvatar>

                                <ListItemText>{userPayload.username}</ListItemText>
                            </ListItemButton>
                        </ListItem>
                    )
                })}
            </List>


            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {userPolicyPayload && userPolicyPayload.administrator &&
                    <Button variant="contained" color="primary" type="submit" onClick={handleNewUser}>
                        New user
                    </Button>
                }
            </div>


        </form>
    )

}

export default Users