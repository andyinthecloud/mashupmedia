import { Button, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useLocation, useNavigate } from "react-router-dom"
import { NotificationType, addNotification } from "../common/notification/notificationSlice"
import { RootState } from "../common/redux/store"
import { FieldValidation, FormValidationPayload, ServerError, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidation, toFieldValidations } from "../common/utils/formValidationUtils"
import { ResetPasswordPayload, resetPassword, stepActivatePassword, stepResetPassword } from "./backend/resetPasswordCalls"

type ResetPasswordPagePayload = {
    formValidationPayload: FormValidationPayload<ResetPasswordPayload>
    hideUsername: boolean
    showActivationField: boolean
}

const ResetPassword = () => {

    const securityPayload = useSelector((state: RootState) => state.security.payload)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)


    const dispatch = useDispatch()

    const enum FieldNames {
        USERNAME = 'username',
        PASSWORD = 'password',
        ACTIVATION_CODE = 'activationCode'
    }

    const [props, setProps] = useState<ResetPasswordPagePayload>(
        {
            formValidationPayload: {
                payload: {
                    username: '',
                    password: ''
                },
                formValidation: {
                    fieldValidations: []
                }
            },
            hideUsername: false,
            showActivationField: false

        }
    )

    const { state } = useLocation()
    useEffect(() => {
        if (!state) {
            return
        }

        setProps(p => ({
            ...p,
            formValidationPayload: {
                ...p.formValidationPayload,
                payload: {
                    ...p.formValidationPayload.payload,
                    username: state.username
                }
            },
            hideUsername: state.username ? true : false

        }))
    }, [state])

    const setFieldValueState = (name: string, value: string): void => {
        setProps(p => ({
            ...p,
            formValidationPayload: {
                ...p.formValidationPayload,
                payload: {
                    ...p.formValidationPayload.payload,
                    [name]: value
                }
            }
        }))
    }

    const setServerFieldValidationState = (serverError: ServerError): void => {
        const fieldValidations = props.formValidationPayload.formValidation.fieldValidations
        fieldValidations.push(toFieldValidation(serverError))

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    const setFieldValidationState = (fieldValidation: FieldValidation): void => {
        const fieldValidations = props.formValidationPayload.formValidation.fieldValidations
        fieldValidations.push(fieldValidation)

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    const clearFieldValidationState = () => {
        const fieldValidations = props.formValidationPayload.formValidation.fieldValidations
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

        if (isEmpty(props.formValidationPayload.payload.password)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.USERNAME, 'Email'))
        }

        if (isEmpty(props.formValidationPayload.payload.password)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.PASSWORD, 'Password'))
        }
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()

        validateForm()

        if (props.formValidationPayload.formValidation.fieldValidations.length) {
            return
        }

        if (userPolicyPayload?.administrator) {
            resetPassword(props.formValidationPayload.payload, securityPayload?.token)
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

                        const errorPayload = response.parsedBody?.errorPayload
                        setProps(p => ({
                            ...p,
                            formValidationPayload: {
                                ...p.formValidationPayload,
                                formValidation: {
                                    fieldValidations: p.formValidationPayload.formValidation.fieldValidations.concat(
                                        toFieldValidations(errorPayload)
                                    )
                                }
                            }
                        }))

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
            stepResetPassword(props.formValidationPayload.payload).then(
                response => {
                    if (response.ok) {
                        const responsePayload = response.parsedBody?.payload
                        if (responsePayload) {
                            setProps(p => ({
                                ...p,
                                payload: responsePayload,
                                showActivationField: true
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
        stepActivatePassword(props.formValidationPayload.payload).then(
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

                    const errorPayload = response.parsedBody?.errorPayload
                    setProps(p => ({
                        ...p,
                        formValidationPayload: {
                            ...p.formValidationPayload,
                            formValidation: {
                                fieldValidations: p.formValidationPayload.formValidation.fieldValidations.concat(
                                    toFieldValidations(errorPayload)
                                )
                            }
                        }
                    }))


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

    const containerClass = (): string => (
        props.hideUsername ? '' : 'zero-top-margin'
    )

    return (
        <form onSubmit={handleSubmit} className={containerClass()}>
            <h1>Reset password</h1>

            {!props.showActivationField && !props.hideUsername &&
                <div className="new-line">
                    <TextField
                        label="Email"
                        value={props.formValidationPayload.payload.username}
                        onChange={(e) => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        name={FieldNames.USERNAME}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.USERNAME, props.formValidationPayload.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidationPayload.formValidation)}
                    />
                </div>
            }

            {!props.showActivationField &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.PASSWORD}
                        label="Password"
                        type={"password"}
                        value={props.formValidationPayload.payload.password}
                        fullWidth={true}
                        onChange={e => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        error={hasFieldError(FieldNames.PASSWORD, props.formValidationPayload.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.PASSWORD, props.formValidationPayload.formValidation)}
                    />
                </div>
            }

            {props.showActivationField &&
                <div className="new-line">
                    <TextField
                        label="Activation code"
                        value={props.formValidationPayload.payload.activationCode || ''}
                        onChange={(e) => setFieldValueState(e.currentTarget.name, e.currentTarget.value)}
                        name={FieldNames.ACTIVATION_CODE}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.USERNAME, props.formValidationPayload.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidationPayload.formValidation)}
                    />
                    <div style={{ fontSize: 'smaller' }}>Please enter the activation code sent to the email address. Please note it is only valid for five minutes.</div>
                </div>
            }

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {!props.showActivationField &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={!props.formValidationPayload.payload.username}>
                        Reset
                    </Button>
                }

                {props.showActivationField &&
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