import { Button, TextField } from "@mui/material"
import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { FieldValidation, FormValidationPayload, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidation } from "../common/utils/formValidationUtils"
import logo from "../logo.png"
import { CreateUserPayload, stepCreateUser } from "./backend/createUserCalls"

const CreateUser = () => {

    const enum FieldNames {
        NAME = 'name',
        USERNAME = 'username',
        PASSWORD = 'password',
    }

    const [props, setProps] = useState<FormValidationPayload<CreateUserPayload>>({
        payload: {
            name: '',
            username: '',
            password: ''
        },
        formValidation: {
            fieldValidations: []
        }
    })

    const setStateValue = (name: string, value: string): void => {

        setProps(p => ({
            ...p,
            payload: {
                ...p.payload,
                [name]: value

            }
        }))
    }

    const navigate = useNavigate()

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


    const validateForm = (): void => {

        clearFieldValidationState()

        const payload = props.payload

        if (isEmpty(payload.name)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.NAME, 'Name'))
        }
        if (isEmpty(payload.username || '')) {
            setFieldValidationState(emptyFieldValidation(FieldNames.USERNAME, 'Email'))
        }
        if (isEmpty(payload.password || '')) {
            setFieldValidationState(emptyFieldValidation(FieldNames.PASSWORD, 'Password'))
        }

    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        validateForm()

        if (props.formValidation.fieldValidations.length) {
            return
        }

        stepCreateUser(props.payload)
            .then(response => {

                const parsedBody = response.parsedBody
                console.log('stepCreate', parsedBody)
                if (response.ok && parsedBody) {
                    navigate('/create-user/activate', {state: parsedBody.payload})
                } else {
                    parsedBody?.errorPayload.fieldErrors.map(function (serverError) {                        
                        props.formValidation.fieldValidations.push(toFieldValidation(serverError))
                        setProps(p => ({
                            ...p,
                            formValidation: {
                                fieldValidations: props.formValidation.fieldValidations 
                            }
                        }))
                    })
                }
            })
    }

    return (
        <form className="zero-top-margin" onSubmit={handleSubmit}>

            <img src={logo} className="logo" alt="Mashup Media" />

            <h1>Create account</h1>

            <div className="new-line">
                <TextField
                    label="Name"
                    value={props.payload.name}
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    name="name"
                    fullWidth={true}
                    error={hasFieldError(FieldNames.NAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.NAME, props.formValidation)}
                />
            </div>

            <div className="new-line">
                <TextField
                    label="Email"
                    value={props.payload.username}
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    name="username"
                    fullWidth={true}
                    error={hasFieldError(FieldNames.USERNAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidation)}
                />
            </div>

            <div className="new-line">
                <TextField
                    name="password"
                    label="Password"
                    value={props.payload.password}
                    autoComplete="off"
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    type={"password"}
                    error={hasFieldError(FieldNames.PASSWORD, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.PASSWORD, props.formValidation)}
                />
            </div>


            <div className="new-line right">
                <Button variant="contained" color="primary" type="submit">
                    Sign up
                </Button>
            </div>
        </form>
    )
}

export default CreateUser