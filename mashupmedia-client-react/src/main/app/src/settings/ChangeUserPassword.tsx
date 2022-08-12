import { Button, TextField } from "@mui/material"
import { useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import FieldError from "../components/FieldError"
import { addNotification, NotificationType } from "../notification/notificationSlice"
import { RootState } from "../redux/store"
import { emptyFieldValidation, FieldValidation, FormValidation, isEmpty } from "../utils/form-validation-utils"
import { changePassword, ChangeUserPasswordPayload } from "./backend/userCalls"

type ChangeUserPasswordPagePayload = {
    changeUserPasswordPayload: ChangeUserPasswordPayload
    formValidation: FormValidation
}

const ChangeUserPassword = () => {

    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    const enum FieldNames {
        CURRENT_PASSWORD = 'currentPassword',
        NEW_PASSWORD = 'newPassword',
        CONFIRM_PASSWORD = 'confirmPassword'
    }

    const [props, setProps] = useState<ChangeUserPasswordPagePayload>(
        {
            changeUserPasswordPayload: {
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
            }, formValidation: { fieldValidations: [] }
        }
    )

    const setFieldValueState = (name: string, value: string): void => {
        setProps(p => ({
            ...p,
            changeUserPasswordPayload: {
                ...p.changeUserPasswordPayload,
                [name]: value
            }
        }))
    }

    const setFieldValidationState = (fieldValidation: FieldValidation): void => {
        const fieldValidations = props.formValidation.fieldValidations
        fieldValidations.push(fieldValidation)

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    const clearFieldValidationState = () => {
        const fieldValidations = props.formValidation.fieldValidations
        fieldValidations.splice(0, fieldValidations.length)
        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }


    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }


    const validateForm = (): void => {

        clearFieldValidationState()

        const changeUserPasswordPayload = props.changeUserPasswordPayload

        if (isEmpty(changeUserPasswordPayload.currentPassword)) {
            setFieldValidationState(emptyFieldValidation('currentPassword', 'Current password'))
        }

    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        validateForm()

        if (props.formValidation.fieldValidations.length) {
            return
        }

        changePassword(props.changeUserPasswordPayload, userToken)
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
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)} />

                <FieldError fieldName="currentPassword" formValidation={props.formValidation}></FieldError>

            </div>

            <div className="new-line">
                <TextField
                    name="newPassword"
                    label="New password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)} />
            </div>

            <div className="new-line">
                <TextField
                    name="confirmPassword"
                    label="Confirm password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)} />
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