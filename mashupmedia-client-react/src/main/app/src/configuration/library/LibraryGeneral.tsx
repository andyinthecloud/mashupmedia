import { useEffect, useState } from "react"
import { LibraryPayload, LibraryTypePayload, checkLibraryPathExists, deleteLibrary, saveLibrary } from "../backend/libraryCalls"
import { useNavigate, useParams } from "react-router-dom"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { FormValidation, ServerError, fieldErrorMessage, hasFieldError, toFieldValidation } from "../../common/utils/formValidationUtils"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { useDispatch } from "react-redux"
import { Button, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, TextField } from "@mui/material"
import { displayDateTime } from "../../common/utils/dateUtils"
import { LibraryPagePayload, TabPanelPayload } from "./Library"

type LibrayValidationPayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    isCorrectMediaPath?: boolean
    tabPanelPayload: TabPanelPayload
}

const LibraryGeneral = (libraryPagePayload: LibraryPagePayload) => {
    const tabIndex = 0

    const enum FieldNames {
        TYPE = 'libraryTypePayload',
        NAME = 'name',
        PATH = 'path',
        CREATED_ON = 'createdOn',
        CREATED_BY = 'createdBy',
        UPDATED_ON = 'updatedOn',
        UPDATED_BY = 'updatedBy'
    }


    const enum MusicFieldNames {
        ART_IMAGE_PATTERN = 'albumArtImagePattern'
    }

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const [props, setProps] = useState<LibrayValidationPayload>({
        libraryPayload: {
            name: '',
            path: '',
            enabled: true,
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        formValidation: {
            fieldValidations: []
        },
        tabPanelPayload: {
            index: tabIndex
        }
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            libraryPayload: libraryPagePayload.libraryPayload,
            tabPanelPayload: {
                index: tabIndex,
                value: libraryPagePayload.tabPanelPayload.value
            }
        }))
    }, [libraryPagePayload])

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


        checkLibraryPathExists(props.libraryPayload.path, userToken)
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
        <div
            hidden={props.tabPanelPayload.value !== props.tabPanelPayload?.index}
        >

            {!props.libraryPayload.id &&
                <div className="new-line">
                    <FormControl fullWidth={true}>
                        <FormLabel className='align-left'>Choose library type</FormLabel>
                        <RadioGroup
                            row
                            aria-labelledby="demo-radio-buttons-
                        -label"
                            defaultValue="music"
                            name={FieldNames.TYPE}
                            onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                        >
                            <FormControlLabel value="music" control={<Radio />} label="Music" />
                            <FormControlLabel value="photo" control={<Radio />} label="Photo" />
                            <FormControlLabel value="video" control={<Radio />} label="Video" />
                        </RadioGroup>
                    </FormControl>
                </div>
            }

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

            {userPolicyPayload?.administrator &&
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

            {props.libraryPayload.createdOn &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.CREATED_ON}
                        label="Created on"
                        value={displayDateTime(props.libraryPayload.createdOn)}
                        fullWidth={true}
                        disabled={true}
                    />
                </div>
            }

            {props.libraryPayload.createdBy &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.CREATED_BY}
                        label="Created by"
                        value={props.libraryPayload.createdBy}
                        fullWidth={true}
                        disabled={true}
                    />
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

            {props.libraryPayload.updatedBy &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.UPDATED_BY}
                        label="Updated by"
                        value={props.libraryPayload.updatedBy}
                        fullWidth={true}
                        disabled={true}
                    />
                </div>
            }

            <div className="new-line right">

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

            </div>

        </div>
    )
}

export default LibraryGeneral