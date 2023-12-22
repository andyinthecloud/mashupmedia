import { Box, Button, Checkbox, FormControlLabel, FormGroup, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { useLocation, useNavigate, useParams } from "react-router-dom"
import Checkboxes from "../common/components/Checkboxes"
import { NotificationType, addNotification } from "../common/notification/notificationSlice"
import type { RootState } from "../common/redux/store"
import { displayDateTime } from "../common/utils/dateUtils"
import { toCheckboxPayloads, toNameValuePayloads, toSelectedValues } from "../common/utils/domainUtils"
import { FormValidation, ServerError, fieldErrorMessage, hasFieldError, toFieldValidation } from "../common/utils/formValidationUtils"
import { HttpStatus, redirectLogin } from "../common/utils/httpUtils"
import { NameValuePayload, getRoles } from "./backend/metaCalls"
import { UserPayload, deleteUserAccount, getMyAccount, saveUserAccount, userAccount } from "./backend/userCalls"

type UserValidationPayload = {
    userPayload: UserPayload
    formValidation: FormValidation
}

const User = () => {

    const enum FieldNames {
        USERNAME = 'username',
        NAME = 'name',
        PASSWORD = 'password',
        REPEAT_PASSWORD = 'repeatPassword',

    }

    const { userId } = useParams();

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<UserValidationPayload>({
        userPayload: {
            enabled: false,
            editable: false,
            name: '',
            username: '',
            administrator: false,
            exists: false
        },
        formValidation: { fieldValidations: [] }
    })

    const location = useLocation()

    useEffect(() => {

        // user account with username in url
        if (location.pathname.match(/\/user-account/) && userId) {
            userAccount(userId, userToken)
                .then((response) => {
                    const userPayload = response.parsedBody !== undefined
                        ? response.parsedBody
                        : null;

                    if (userPayload != null) {
                        setProps(p => ({
                            ...p,
                            userPayload
                        })
                        )
                    }
                })
                .catch(() => redirectLogin(HttpStatus.FORBIDDEN))
        }
        // new account
        else if (location.pathname.match(/\/new-account/)) {
            setProps(p => ({
                ...p,
                userPayload: {
                    administrator: false,
                    editable: true,
                    enabled: true,
                    name: '',
                    username: '',
                    password: '',
                    exists: false
                }
            }))
        }
        // my account
        else {
            getMyAccount(userToken)
                .then((response) => {

                    const userPayload = response.parsedBody !== undefined
                        ? response.parsedBody
                        : null;

                    if (userPayload != null) {
                        setProps(p => ({
                            ...p,
                            userPayload
                        }))
                    }
                })
                .catch(() => redirectLogin(HttpStatus.FORBIDDEN))

        }

    }, [userToken, location, userId])

    const [rolePayloads, setRolePayloads] = useState<NameValuePayload<string>[]>([])

    useEffect(() => {

        getRoles(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setRolePayloads(response.parsedBody)
            }
        })

    }, [userToken])

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)



    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            userPayload: {
                ...p.userPayload,
                [name]: value
            }
        }))
    }

    const isEditable = (): boolean => (!props.userPayload.editable)

    const handleRolesChange = (values: string[]) => {
        setStateValue('rolePayloads', toNameValuePayloads(values))
    }

    const dispatch = useDispatch()

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

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        saveUserAccount(props.userPayload, userToken)
            .then(response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'User updated.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/configuration/users')
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

    const navigate = useNavigate()
    function handleCancel(): void {
        navigate('/')
    }

    function handleChangeUserPassword(): void {
        navigate(`/configuration/change-user-password/${props.userPayload.username}`)
    }

    function handleDeleteUser(): void {
        deleteUserAccount(props.userPayload.username, userToken)
            .then(() => {
                dispatch(
                    addNotification({
                        message: 'Account deleted',
                        notificationType: NotificationType.SUCCESS
                    })
                )
            })
            .catch(() => {
                dispatch(
                    addNotification({
                        message: 'Unable to delete account.',
                        notificationType: NotificationType.ERROR
                    })
                )
            })
        navigate('/configuration/users')
    }

    const hasDeletePermission = (): boolean => {
        if (userPolicyPayload?.username == props.userPayload.username) {
            return false
        }

        if (userPolicyPayload?.administrator) {
            return userPolicyPayload.administrator
        }

        return false
    }


    const hasChangePasswordPermission = (): boolean => {
        if (userPolicyPayload?.username == props.userPayload.username) {
            return true
        }

        if (userPolicyPayload?.administrator) {
            return userPolicyPayload.administrator
        }

        return false
    }

    return (
        <form onSubmit={handleSubmit}>
            <h1>Edit user</h1>


            <div className="new-line">
                <Box sx={{ color: 'primary.main' }}>
                    <FormGroup>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    disabled={isEditable()}
                                    name="enabled"
                                    checked={props.userPayload.enabled}
                                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.checked)}
                                />}
                            label="Enabled" />
                    </FormGroup>
                </Box>
            </div>

            <div className="new-line">
                <TextField
                    name={FieldNames.USERNAME}
                    label="Username"
                    disabled={isEditable()}
                    value={props.userPayload.username}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.USERNAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.USERNAME, props.formValidation)}
                />
            </div>


            {!props.userPayload.exists &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.PASSWORD}
                        label="Password"
                        disabled={isEditable()}
                        value={props.userPayload.password}
                        onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.PASSWORD, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.PASSWORD, props.formValidation)}
                        type="password"
                    />
                </div>
            }

            {!props.userPayload.exists &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.REPEAT_PASSWORD}
                        label="Repeat password"
                        disabled={isEditable()}
                        value={props.userPayload.repeatPassword}
                        onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.REPEAT_PASSWORD, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.REPEAT_PASSWORD, props.formValidation)}
                        type="password"
                    />
                </div>
            }

            <div className="new-line">
                <TextField
                    name={FieldNames.NAME}
                    label="Name"
                    disabled={isEditable()}
                    value={props.userPayload.name}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.NAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.NAME, props.formValidation)}
                />
            </div>

            {props.userPayload.createdOn &&
                <div className="new-line">
                    <TextField
                        label="Created on"
                        disabled={true}
                        value={displayDateTime(props.userPayload.createdOn)}
                        fullWidth={true} />
                </div>
            }

            {props.userPayload.updatedOn &&
                <div className="new-line">
                    <TextField
                        label="Updated on"
                        disabled={true}
                        value={displayDateTime(props.userPayload.updatedOn)}
                        fullWidth={true} />
                </div>
            }

            <div className="new-line">
                <h2>Roles</h2>
                <Checkboxes<string>
                    isDisabled={!props.userPayload.editable}
                    referenceItems={toCheckboxPayloads<string>(rolePayloads)}
                    selectedValues={toSelectedValues<string>(props.userPayload.rolePayloads)}
                    onChange={handleRolesChange}
                />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {hasChangePasswordPermission() && props.userPayload.exists &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleChangeUserPassword}>
                        Change password
                    </Button>
                }

                {hasDeletePermission() &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleDeleteUser}>
                        Delete
                    </Button>
                }

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="primary" type="submit" disabled={!props.userPayload.editable}>
                        Save
                    </Button>
                }
            </div>
        </form>
    )


}


export default User