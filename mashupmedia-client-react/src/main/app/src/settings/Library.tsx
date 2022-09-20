import { Check } from "@mui/icons-material";
import { Button, FormControl, FormControlLabel, FormLabel, IconButton, Radio, RadioGroup, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import Checkboxes from "../common/components/Checkboxes";
import { addNotification, NotificationType } from '../common/notification/notificationSlice';
import { RootState } from "../common/redux/store";
import { toCheckboxPayloads, toNameValuePayloads, toSelectedValues } from "../common/utils/domainUtils";
import { fieldErrorMessage, FormValidation, hasFieldError, ServerError, toFieldValidation } from "../common/utils/form-validation-utils";
import { checkLibraryPathExists, deleteLibrary, getLibrary, LibraryPayload, LibraryTypePayload, saveLibrary } from "./backend/libraryCalls";
import { getGroups, NameValuePayload } from "./backend/metaCalls";
import './Library.css';
import { displayDateTime } from "../common/utils/dateUtils"



type LibrayValidationPayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    groupPayloads: NameValuePayload<number>[]
    isCorrectMediaPath?: boolean
}

const Library = () => {

    const enum FieldNames {
        TYPE = 'libraryTypePayload',
        NAME = 'name',
        PATH = 'path',
        GROUPS = 'groups',
        CREATED_ON = 'createdOn',
        CREATED_BY = 'createdBy',
        UPDATED_ON = 'updatedOn',
        UPDATED_BY = 'updatedBy'
    }


    const enum MusicFieldNames {
        ART_IMAGE_PATTERN = 'albumArtImagePattern'
    }

    const { libraryId } = useParams()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const [props, setProps] = useState<LibrayValidationPayload>({
        libraryPayload: {
            name: '',
            path: '',
            enabled: true,
            groups: [],
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        formValidation: {
            fieldValidations: []
        },
        groupPayloads: []
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

        getGroups(userToken)
            .then(response => {
                const groupPayloads = response.parsedBody || null

                if (groupPayloads) {
                    setProps(p => ({
                        ...p,
                        groupPayloads
                    }))
                }
            })

    }, [libraryId, userToken])

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            libraryPayload: {
                ...p.libraryPayload,
                [name]: value
            }
        }))
    }

    const handleGroupsChange = (values: number[]) => {
        setStateValue('groups', toNameValuePayloads(values))
    }

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/settings/libraries')
    }

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

        navigate('/settings/libraries')
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

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()

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
                    navigate('/settings/libraries')
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

    return (
        <form onSubmit={handleSubmit}>
            <h1>Library</h1>


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

            <div className="new-line">
                <TextField
                    name={FieldNames.PATH}
                    label="Media folder path"
                    value={props.libraryPayload.path}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true}
                    error={hasFieldError(FieldNames.PATH, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.PATH, props.formValidation)}
                    InputProps={{ endAdornment: <IconButton onClick={handleCheckPath}><Check /></IconButton> }}
                />
            </div>

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

            {props.libraryPayload.libraryTypePayload === LibraryTypePayload.MUSIC &&
                <div className="new-line">
                    <TextField
                        name={MusicFieldNames.ART_IMAGE_PATTERN}
                        label="Art image file pattern"
                        value={props.libraryPayload.updatedOn}
                        onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                        fullWidth={true}
                        helperText={'Something like this'}
                    />
                </div>
            }


            <div className="new-line">
                <h2>Groups</h2>
                <Checkboxes<number>
                    referenceItems={toCheckboxPayloads<number>(props.groupPayloads)}
                    selectedValues={toSelectedValues<number>(props.libraryPayload.groups)}
                    onChange={handleGroupsChange}
                    error={hasFieldError(FieldNames.GROUPS, props.formValidation)}
                    helperText={fieldErrorMessage(FieldNames.GROUPS, props.formValidation)}
                />
            </div>


            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {userPolicyPayload?.administrator &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleDeleteLibrary}>
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

export default Library