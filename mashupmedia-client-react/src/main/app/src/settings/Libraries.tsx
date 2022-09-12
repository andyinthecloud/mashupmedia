import { Button, List, ListItem, ListItemButton, ListItemText } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../common/redux/store"
import { getLibraries, LibraryNameValuePayload } from "./backend/libraryCalls"

const  Libraries = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const [props, setProps] = useState<LibraryNameValuePayload[]>([])

    useEffect(() => {
        getLibraries(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
            }
        })

    }, [userToken])

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }

    function handleNewLibrary(): void {
        navigate('/settings/library')
    }

    function handleClickLibrary(libraryId: number): void {
        navigate('/settings/library/' + libraryId)
    }

    return (
        <form>
            <h1>Libraries</h1>

            <List>
                {props.map(function (library) {
                    return (
                        <ListItem key={library.value} onClick={() => handleClickLibrary(library.value)}>
                            <ListItemButton>
                                <ListItemText>{library.name}</ListItemText>
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
                    <Button variant="contained" color="primary" type="submit" onClick={handleNewLibrary}>
                        New library
                    </Button>
                }
            </div>
        </form>
    )
}

export default Libraries