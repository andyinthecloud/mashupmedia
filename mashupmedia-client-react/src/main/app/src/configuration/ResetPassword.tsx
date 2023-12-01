import { Button, TextField } from "@mui/material"
import { useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { NotificationType, addNotification } from "../common/notification/notificationSlice"
import { RootState } from "../common/redux/store"
import { FieldValidation, FormValidationPayload, ServerError, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidation } from "../common/utils/formValidationUtils"
import { ResetPasswordPayload, resetPassword, stepActivatePassword, stepResetPassword } from "./backend/resetPasswordCalls"

const ResetPassword = () => {

    const securityPayload = useSelector((state: RootState) => state.security.payload)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)


    const dispatch = useDispatch()

    const enum FieldNames {
        USERNAME = 'username',
        PASSWORD = 'password',
        ACTIVATION_CODE = 'activationCode'
    }

    const [props, setProps] = useState<FormValidationPayload<ResetPasswordPayload>>(
        {
            payload: {
                username: '',
                password: ''
            },
            formValidation: {
                fieldValidations: []
            }
        }
    )

    const [showActivationField, setShowActivationField] = useState<boolean>(false)

    const setFieldValueState = (name: string, value: string): void => {
        setProps(p => ({
            ...p,
            payload: {
                ...p.payload,
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
        navigate('/configuration/users')
    }


    const validateForm = (): void => {

        clearFieldValidationState()

        if (isEmpty(props.payload.password)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.USERNAME, 'Email'))
        }

        if (isEmpty(props.payload.password)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.PASSWORD, 'Password'))
        }
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()

        validateForm()

        if (props.formValidation.fieldValidations.length) {
            return
        }

        if (userPolicyPayload?.administrator) {
            console.log('admin')
            resetPassword(props.payload, securityPayload?.token)
                .then(response => {
                    if (response.ok) {
                        dispatch(
                            addNotification({
                                message: 'Reset password.',
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
        } else {
            stepResetPassword(props.payload).then(
                response => {
                    if (response.ok) {
                        const responsePayload = response.parsedBody?.payload
                        if (responsePayload) {
                            setShowActivationField(true)
                            setProps(p => ({
                                ...p,
                                payload: responsePayload
                            }))

                        }
                    } else {
                        response.parsedBody?.errorPayload.fieldErrors.map(function (serverError) {
                            setServerFieldValidationState(serverError)
                        })
                    }
                }
            )
        }
    }

    const handleActvate = (): void => {
        stepActivatePassword(props.payload).then(
            response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Reset password.',
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
            }
        )
    }

    const hasChangePasswordPermission = (): boolean => {
        if (userPolicyPayload?.administrator) {
            return userPolicyPayload.administrator
        }

        if (userPolicyPayload?.username == props.payload.username) {
            return true
        }

        return false
    }

    const containerClass = (): string => (
        securityPayload?.token ? '' : 'zero-top-margin'
    )

    return (
        <form onSubmit={handleSubmit} className={containerClass()}>
            <h1>Reset password</h1>

            {!showActivationField &&
                <div className="new-line">
                    <TextField
                        label="Email"
                        value={props.payload.username}
                        onChange={(e) => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        name={FieldNames.USERNAME}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.USERNAME, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidation)}
                    />
                </div>
            }

            {!showActivationField &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.PASSWORD}
                        label="Password"
                        type={"password"}
                        value={props.payload.password}
                        fullWidth={true}
                        onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        error={hasFieldError(FieldNames.PASSWORD, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.PASSWORD, props.formValidation)}
                    />
                </div>
            }

            {showActivationField &&
                <div className="new-line">
                    <TextField
                        label="Activation code"
                        value={props.payload.activationCode || ''}
                        onChange={(e) => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        name={FieldNames.ACTIVATION_CODE}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.USERNAME, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidation)}
                    />
                    <div style={{ fontSize: 'smaller' }}>Please enter the activation code sent to the email address. Please note it is only valid for five minutes.</div>
                </div>
            }

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {!showActivationField &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={!props.payload.username}>
                        Reset
                    </Button>
                }

                {showActivationField &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="button"
                        onClick={handleActvate}>
                        Activate
                    </Button>
                }


            </div>

        </form>
    )
}

export default ResetPassword