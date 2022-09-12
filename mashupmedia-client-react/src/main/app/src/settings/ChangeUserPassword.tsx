import { Button, TextField } from "@mui/material"
import { useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useNavigate, useParams } from "react-router-dom"
import { addNotification, NotificationType } from "../common/notification/notificationSlice"
import { RootState } from "../common/redux/store"
import { emptyFieldValidation, fieldErrorMessage, FieldValidation, FormValidation, hasFieldError, isEmpty, ServerError, toFieldValidation } from "../common/utils/form-validation-utils"
import { changePassword, ChangeUserPasswordPayload } from "./backend/userCalls"

type ChangeUserPasswordPagePayload = {
    changeUserPasswordPayload: ChangeUserPasswordPayload
    formValidation: FormValidation
}

const ChangeUserPassword = () => {

    const securityPayload = useSelector((state: RootState) => state.security.payload)


    const dispatch = useDispatch()

    const enum FieldNames {
        CURRENT_PASSWORD = 'currentPassword',
        NEW_PASSWORD = 'newPassword',
        CONFIRM_PASSWORD = 'confirmPassword'
    }

    const { userId } = useParams()

    const [props, setProps] = useState<ChangeUserPasswordPagePayload>(
        {
            changeUserPasswordPayload: {
                currentPassword: '',
                newPassword: '',
                confirmPassword: '',
                username: userId
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

    const setServerFieldValidationState = (serverError: ServerError): void => {
        const fieldValidations = props.formValidation.fieldValidations
        fieldValidations.push(toFieldValidation(serverError))

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
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
        navigate('/settings/users')
    }


    const validateForm = (): void => {

        clearFieldValidationState()

        const changeUserPasswordPayload = props.changeUserPasswordPayload

        if (isEmpty(changeUserPasswordPayload.currentPassword)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.CURRENT_PASSWORD, 'Current password'))
        }

        if (isEmpty(changeUserPasswordPayload.newPassword)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.NEW_PASSWORD, 'New password'))
        }

        if (isEmpty(changeUserPasswordPayload.confirmPassword)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.CONFIRM_PASSWORD, 'Confirm password'))
        }

    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()

        validateForm()

        if (props.formValidation.fieldValidations.length) {
            return
        }

        changePassword(props.changeUserPasswordPayload, securityPayload?.token)
            .then(response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Password saved.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/')

                } else {
                    response.parsedBody?.errorPayload.fieldErrors.map(function (serverError) {
                        setServerFieldValidationState(serverError)
                    })

                    response.parsedBody?.errorPayload.objectErrors.map(function (serverError) {
                        dispatch(
                            addNotification({
                                message: serverError.defaultMessage,
                                notificationType: NotificationType.ERROR
                            })
                        )
                    })
                }
            })
    }

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const hasChangePasswordPermission = (): boolean => {
        if (userPolicyPayload?.administrator) {
            return userPolicyPayload.administrator
        }

        if (userPolicyPayload?.username == props.changeUserPasswordPayload.username) {
            return true
        }

        return false
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Change user password</h1>

            <div className="new-line">
                <TextField
                    name={FieldNames.CURRENT_PASSWORD}
                    label="Current password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                    error={hasFieldError(FieldNames.CURRENT_PASSWORD, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.CURRENT_PASSWORD, props.formValidation)}
                />
            </div>

            <div className="new-line">
                <TextField
                    name={FieldNames.NEW_PASSWORD}
                    label="New password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                    error={hasFieldError(FieldNames.NEW_PASSWORD, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.NEW_PASSWORD, props.formValidation)}
                />
            </div>

            <div className="new-line">
                <TextField
                    name={FieldNames.CONFIRM_PASSWORD}
                    label="Confirm password"
                    type={"password"}
                    fullWidth={true}
                    onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                    error={hasFieldError(FieldNames.CONFIRM_PASSWORD, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.CONFIRM_PASSWORD, props.formValidation)}
                />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {hasChangePasswordPermission() &&
                    <Button variant="contained" color="primary" type="submit">
                        Change password
                    </Button>
                }

            </div>

        </form>
    )
}

export default ChangeUserPassword