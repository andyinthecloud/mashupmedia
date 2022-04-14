import { Box, Checkbox, FormControlLabel, FormGroup, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useAppDispatch } from "../redux/hooks"
import type { RootState } from "../redux/store"
import { getMyAccount, UserPayload } from "./features/userSlice"

const MyAccount = () => {

    const dispatch = useAppDispatch()
    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    useEffect(() => {
        dispatch(
            getMyAccount(userToken)
        )
    }, [userToken, dispatch])

    const userPayload = useSelector((state: RootState) => state.user.payload)
    const userPayloadAction = useSelector((state: RootState) => state.user.payloadAction)

    const [props, setProps] = useState<UserPayload>({
        admin: false,
        enabled: false,
        name: '',
        username: ''
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            ...userPayload
        }))

    }, [userPayload])

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    const isAdministrator = () => true
    const isMyAccount = () => true

    return (
        <form>
            <h1>Edit user</h1>


            <div className="new-line">
                <Box sx={{ color: 'primary.main' }}>
                <FormGroup>
                    <FormControlLabel control={<Checkbox defaultChecked />} label="Administrator" />
                    <FormControlLabel disabled control={<Checkbox />} label="Enabled" />
                </FormGroup>
                </Box>
            </div>

            <div className="new-line">
                <TextField name="username" label="Username" 
                    fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField name="name" label="Name" 
                    fullWidth={true} />
            </div>



        </form>
    )



}


export default MyAccount