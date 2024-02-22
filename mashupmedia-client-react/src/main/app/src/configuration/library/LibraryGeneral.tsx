import { useEffect, useState } from "react"
import { LibraryPayload, LibraryTypePayload, checkLibraryPathExists, deleteLibrary, saveLibrary } from "../backend/libraryCalls"
import { useNavigate, useParams } from "react-router-dom"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { FormValidation, ServerError, fieldErrorMessage, hasFieldError, toFieldValidation } from "../../common/utils/formValidationUtils"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { useDispatch } from "react-redux"
import { Button, Checkbox, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, TextField } from "@mui/material"
import { displayDateTime } from "../../common/utils/dateUtils"
import { LibraryPagePayload } from "./Library"
import { triggerSaveLibrary } from "./features/librarySlice"

type LibrayValidationPayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    isCorrectMediaPath?: boolean
}

const LibraryGeneral = (libraryPagePayload: LibraryPagePayload) => {

    const enum FieldNames {
        TYPE = 'libraryTypePayload',
        NAME = 'name',
        ENABLED = 'enabled',
        PRIVATE_ACCESS = 'privateAccess',
        PATH = 'path',
        UPDATED_ON = 'updatedOn',
    }


    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const libraryRefreshPayload = useSelector((state: RootState) => state.libraryRefresh)

    const [props, setProps] = useState<LibrayValidationPayload>({
        libraryPayload: {
            name: '',
            privateAccess: false,
            path: '',
            enabled: true,
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        formValidation: {
            fieldValidations: []
        }
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            libraryPayload: libraryPagePayload.libraryPayload
        }))
    }, [libraryPagePayload])

    useEffect(() => {

        console.log('libraryRefreshPayload.triggerSave', libraryRefreshPayload.triggerSave)

        if (libraryRefreshPayload.triggerSave) {
            handleSave()
        }
    }, [libraryRefreshPayload.triggerSave])

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            libraryPayload: {
                ...p.libraryPayload,
                [name]: value
            }
        }))
    }

    const navigate = useNavigate()

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

    function handleSave(): void {

        dispatch(
            triggerSaveLibrary({
                triggerSave: null
            })
        )

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

    const dispatch = useDispatch()

    function handleCheckPath(): void {
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


        checkLibraryPathExists(props.libraryPayload.path || '', userToken)
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

    const isShowSaveButton = (): boolean => {
        return userPolicyPayload?.administrator || false
    }

    return (
        <div>

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

            {userPolicyPayload?.administrator &&
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

            {/* <div className="new-line right">

                {isShowSaveButton() &&
                    <Button
                        variant="contained"
                        color="primary"
                        type="button"
                        onClick={handleSave}
                    >
                        Save
                    </Button>
                }

            </div> */}

        </div>
    )
}

export default LibraryGeneral