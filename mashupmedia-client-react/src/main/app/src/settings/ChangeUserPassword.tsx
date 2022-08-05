import { Button, TextField } from "@mui/material"
import { useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { NotificationType, addNotification } from "../notification/notificationSlice"
import { RootState } from "../redux/store"
import { changePassword, ChangeUserPasswordPayload } from "./backend/userCalls"

const ChangeUserPassword = () => {

    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    const [props, setProps] = useState<ChangeUserPasswordPayload>({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    })

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }


    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        changePassword(props, userToken)
            .then(() => addNotification({
                message: 'Password saved.',
                notificationType: NotificationType.SUCCESS
            }))
            .catch(() => addNotification({
                message: 'Please confirm the current password is correct and the new and confirm password are identical.',
                notificationType: NotificationType.ERROR
            }))

    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Change user password</h1>

            <div className="new-line">
                <TextField
                    name="currentPassword"
                    label="Current password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)} />
            </div>

            <div className="new-line">
                <TextField
                    name="newPassword"
                    label="New password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)} />
            </div>

            <div className="new-line">
                <TextField
                    name="confirmPassword"
                    label="Confirm password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)} />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                <Button variant="contained" color="primary" type="submit">
                    Change password
                </Button>
            </div>

        </form>
    )
}

export default ChangeUserPassword