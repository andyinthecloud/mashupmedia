import { Button, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import { NotificationType, addNotification } from "../common/notification/notificationSlice"
import { useAppDispatch } from "../common/redux/hooks"
import { logIn } from "../common/security/features/securitySlice"
import { FieldValidation, FormValidationPayload, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidations } from "../common/utils/formValidationUtils"
import { CreateUserPayload, stepActivateUser } from "./backend/createUserCalls"



const ActivateUser = () => {

    const enum FieldNames {
        ACTIVATION_CODE = 'activationCode'
    }

    const { state } = useLocation()

    const [props, setProps] = useState<FormValidationPayload<CreateUserPayload>>({
        payload: {
            name: '',
            username: ''
        },
        formValidation: {
            fieldValidations: []
        }
    })


    useEffect(() => {
        setProps(p => ({
            ...p,
            payload: state
        }))

    }, [state])

    const handleChangeActivationCode = (value: string) => {
        setProps(
            p => ({
                ...p,
                payload: {
                    ...p.payload,
                    activationCode: value
                }
            })
        )
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

    const validateForm = (): void => {

        clearFieldValidationState()

        const payload = props.payload

        if (isEmpty(payload.activationCode)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.ACTIVATION_CODE, 'Activation code'))
        }

    }

    const navigate = useNavigate()
    const dispatch = useAppDispatch()

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        validateForm()

        if (props.formValidation.fieldValidations.length > 0) {
            return
        }

        stepActivateUser(props.payload)
            .then(response => {

                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Account activated.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )

                    dispatch(
                        logIn({ username: props.payload.username, password: props.payload.password || '' })
                    )

                    navigate('/')
                } else {
                    const errorPayload = response.parsedBody?.errorPayload
                    setProps(p => ({
                        ...p,
                        formValidation: {
                            fieldValidations: p.formValidation.fieldValidations.concat(
                                toFieldValidations(errorPayload)
                            )
                        }
                    }))

                }
            })
    }

    const handleCancel = () => {
        navigate('/')
    }


    return (
        <form className="zero-top-margin" onSubmit={handleSubmit}>

            <h1>Activate account</h1>

            <p>Check your email and enter the activation code below. The code is only valid for five minutes.</p>

            <div className="new-line">
                <TextField
                    label="Activation code"
                    value={props.payload.activationCode || ''}
                    onChange={(e) => handleChangeActivationCode(e.currentTarget.value)}
                    name={FieldNames.ACTIVATION_CODE}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.ACTIVATION_CODE, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.ACTIVATION_CODE, props.formValidation)}
                />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>
                <Button variant="contained" color="primary" type="submit">
                    Activate
                </Button>
            </div>

        </form>
    )
}

export default ActivateUser