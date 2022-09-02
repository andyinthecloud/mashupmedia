import { Button, List, ListItem, ListItemButton, ListItemText } from "@mui/material"
import { number } from "prop-types"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../redux/store"
import { getGroups, NameValuePayload } from "./backend/metaCalls"

const Groups = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const [props, setProps] = useState<NameValuePayload<number>[]>([])


    useEffect(() => {
        getGroups(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
            }
        })

    }, [userToken])

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }

    function handleNewGroup(): void {
        navigate('/')
    }

    function handleClickGroup(id: number): void {
        navigate('/settings/group/' + number)
    }

    return (
        <form>
            <h1>Groups</h1>

            <List>
                {props.map(function (nameValuePayload) {
                    return (
                        <ListItem key={nameValuePayload.value} onClick={() => handleClickGroup(nameValuePayload.value)}>
                            <ListItemButton>
                                <ListItemText>{nameValuePayload.name}</ListItemText>
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
                    <Button variant="contained" color="primary" type="submit" onClick={handleNewGroup}>
                        New group
                    </Button>
                }
            </div>


        </form>
    )
}

export default Groups