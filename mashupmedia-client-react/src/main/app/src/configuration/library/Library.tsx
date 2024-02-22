import { Button, Checkbox, FormControlLabel, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { NotificationType, addNotification } from '../../common/notification/notificationSlice';
import { RootState } from "../../common/redux/store";
import { FormValidation, ServerError, fieldErrorMessage, hasFieldError, toFieldValidation } from "../../common/utils/formValidationUtils";
import { LibraryPayload, LibraryTypePayload, LocationTypePayload, checkLibraryPathExists, deleteLibrary, getLibrary, saveLibrary } from "../backend/libraryCalls";
import './Library.css';
import LibraryUsers from "./LibraryUsers";


export type LibraryPagePayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    isCorrectMediaPath?: boolean
}

const Library = () => {

    const enum FieldNames {
        TYPE = 'libraryTypePayload',
        NAME = 'name',
        ENABLED = 'enabled',
        PRIVATE_ACCESS = 'privateAccess',
        PATH = 'path',
        UPDATED_ON = 'updatedOn',
    }


    const { libraryId } = useParams()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    // const [props, setProps] = useState<LibraryPagePayload>({

    //     libraryPayload: {
    //         name: '',
    //         privateAccess: false,
    //         path: '',
    //         enabled: true,
    //         libraryTypePayload: LibraryTypePayload.MUSIC
    //     }
    // })


    const [props, setProps] = useState<LibraryPagePayload>({
        libraryPayload: {
            name: '',
            privateAccess: false,
            enabled: true,
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        formValidation: {
            fieldValidations: []
        }
    })

    useEffect(() => {
        if (libraryId) {
            getLibrary(+libraryId, userToken)
                .then(response => {
                    const libraryPayload = response.parsedBody || null

                    if (libraryPayload) {
                        setProps(p => ({
                            ...p,
                            libraryPayload
                        }))
                    }
                })
        }

    }, [libraryId, userToken])

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/configuration/libraries')
    }

    const dispatch = useDispatch()

    function handleDeleteLibrary(): void {

        const libraryId = props.libraryPayload.id;
        if (!libraryId) {
            return;
        }

        deleteLibrary(libraryId, userToken)
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

        navigate('/configuration/libraries')
    }

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const isShowDeleteButton = (): boolean => {
        return userPolicyPayload?.administrator || false
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

    const handleSave = () => {
        // dispatch(
        //     triggerSaveLibrary({
        //         triggerSave: new Date().getTime()
        //     })
        // )

        clearFieldValidationState()

        saveLibrary(props.libraryPayload, userToken)
            .then(response => {
                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Library is saved.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                    navigate('/configuration/libraries')
                } else {
                    response.parsedBody?.errorPayload.fieldErrors.map(function (serverError) {
                        setServerFieldValidationState(serverError)
                    })
                }
            })
    }



    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            libraryPayload: {
                ...p.libraryPayload,
                [name]: value
            }
        }))
    }

    function handleCheckPath(): void {

        const path = props.libraryPayload.path;
        if (!path) {
            return;
        }

        const fieldValidations = props.formValidation.fieldValidations
        fieldValidations.splice(
            fieldValidations.findIndex(fv => fv.name == FieldNames.PATH)
            , 1
        )

        setProps(p => ({
            ...p,
            formValidation: {
                fieldValidations
            }
        }))


        checkLibraryPathExists(path, userToken)
            .then(response => {

                if (response.ok) {
                    dispatch(
                        addNotification({
                            message: 'Library path is verified.',
                            notificationType: NotificationType.SUCCESS
                        })
                    )
                } else {
                    response.parsedBody?.errorPayload.fieldErrors.map(function (serverError) {
                        setServerFieldValidationState({ name: 'path', defaultMessage: serverError.defaultMessage })
                    })
                }
            })
    }

    return (
        <form>
            <h1>Library</h1>


            <div className="new-line">
                <FormControlLabel
                    control={<Checkbox
                        value={props.libraryPayload.enabled}
                        checked={props.libraryPayload.enabled}
                        onChange={e => setStateValue(FieldNames.ENABLED, e.currentTarget.checked)}
                    />}
                    label="Enabled" />
            </div>

            <div className="new-line">
                <TextField
                    name={FieldNames.NAME}
                    label="Name"
                    value={props.libraryPayload.name}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.NAME, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.NAME, props.formValidation)}
                />
            </div>

            {userPolicyPayload?.administrator && props.libraryPayload.path &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.PATH}
                        label="Media folder path"
                        value={props.libraryPayload.path}
                        onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.PATH, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.PATH, props.formValidation)}
                    />
                </div>
            }

            {userPolicyPayload?.administrator && props.libraryPayload.path &&
                <div className="new-line right">
                    <Button variant="contained" color="secondary" type="button" onClick={handleCheckPath}>
                        Check path
                    </Button>
                </div>
            }

            {props.libraryPayload.updatedOn &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.UPDATED_ON}
                        label="Updated on"
                        value={props.libraryPayload.updatedOn}
                        fullWidth={true}
                        disabled={true}
                    />
                </div>
            }


            <div className="new-line">
                <FormControlLabel
                    control={<Checkbox
                        value={props.libraryPayload.privateAccess}
                        checked={props.libraryPayload.privateAccess || false}
                        onChange={e => setStateValue(FieldNames.PRIVATE_ACCESS, e.currentTarget.checked)}
                    />}
                    label="Private access" />
            </div>

            {/* <LibraryFiles {...props} /> */}

            {!props.libraryPayload.privateAccess &&
                <LibraryUsers {...props} />
            }

            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {isShowDeleteButton() &&
                    <Button variant="contained" color="primary" type="button" onClick={handleDeleteLibrary}>
                        Delete
                    </Button>
                }
                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                    onClick={handleSave}
                >
                    Save
                </Button>
            </div>

        </form>
    )
}

export default Library