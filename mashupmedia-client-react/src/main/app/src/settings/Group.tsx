import { Button, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { addNotification, NotificationType } from "../notification/notificationSlice";
import { RootState } from "../redux/store";
import { emptyFieldValidation, fieldErrorMessage, FieldValidation, FormValidation, hasFieldError, isEmpty, ServerError, toFieldValidation } from "../utils/form-validation-utils";
import { deleteGroup, getGroup, getGroups, NameValuePayload, saveGroup } from "./backend/metaCalls";

type GroupValidationPayload = {
    groupPayload: NameValuePayload<number>
    formValidation: FormValidation
}

const Group = () => {

    const enum FieldNames {
        NAME = 'name',
        VALUE = 'value',
    }

    const { groupId } = useParams();
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)


    const [props, setProps] = useState<GroupValidationPayload>({
        groupPayload: {
            name: '',
            value: 0
        },
        formValidation: { fieldValidations: [] }
    })

    useEffect(() => {

        if (groupId) {
            getGroup(+groupId, userToken)
                .then(response => {
                    const groupPayload = response.parsedBody
                        ? response.parsedBody
                        : null

                    if (groupPayload) {
                        setProps(p => ({
                            ...p,
                            groupPayload
                        }))
                    }
                })
        }

    }, [userToken, groupId])


    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            groupPayload: {
                ...p.groupPayload,
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


    const validateForm = (): void => {

        clearFieldValidationState()

        const groupPayload = props.groupPayload

        if (isEmpty(groupPayload.name)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.NAME, 'Name'))
        }

    }

    const setServerFieldValidationState = (serverError: ServerError): void => {
        const fieldValidations = props.formValidation.fieldValidations

        console.log('setServerFieldValidationState: serverError', serverError)
        console.log('setServerFieldValidationState', toFieldValidation(serverError))

        fieldValidations.push(toFieldValidation(serverError))

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))
    }

    const dispatch = useDispatch()

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        validateForm()

        saveGroup(props.groupPayload, userToken)
            .then(response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Group saved.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/settings/groups')
                } else {
                    response.parsedBody?. errorPayload.fieldErrors.map(function (serverError) {
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
        navigate('/settings/groups')
    }

    function handleDeleteGroup(): void {
        deleteGroup(props.groupPayload.value, userToken)
            .then(() => {
                dispatch(
                    addNotification({
                        message: 'Group deleted',
                        notificationType: NotificationType.SUCCESS
                    })
                )
            })
            .catch(() => {
                dispatch(
                    addNotification({
                        message: 'Unable to delete group.',
                        notificationType: NotificationType.ERROR
                    })
                )
            })

        navigate('/settings/groups')
    }

    return (
        <form onSubmit={handleSubmit}>

            <h1>Group</h1>

            <div className="new-line">

                <TextField
                    name={FieldNames.NAME}
                    label="Name"
                    value={props.groupPayload.name}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.NAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.NAME, props.formValidation)}
                />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleDeleteGroup}>
                        Delete
                    </Button>
                }

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="primary" type="submit">
                        Save
                    </Button>
                }

            </div>

        </form>
    )
}

export default Group