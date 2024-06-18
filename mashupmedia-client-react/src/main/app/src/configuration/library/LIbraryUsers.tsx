import { Delete, Person, VerifiedUser } from "@mui/icons-material"
import { Avatar, Button, IconButton, List, ListItem, ListItemAvatar, ListItemText, TextField, Tooltip } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"
import { FieldValidation, FormValidation, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidation } from "../../common/utils/formValidationUtils"
import { LibraryPayload, LibraryTypePayload } from "../backend/libraryCalls"
import { LibraryShareUserPayload, addLibraryShare, deleteLibraryShare, getLibraryShares } from "../backend/libraryShareCalls"
import { LibraryPagePayload } from "./Library"


type LibrayUsersPayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    shareUsername?: string
    libraryShareUserPayloads?: LibraryShareUserPayload[]
}



const LibraryUsers = (libraryPagePayload: LibraryPagePayload) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const enum FieldNames {
        EMAIL = 'email'
    }

    const [props, setProps] = useState<LibrayUsersPayload>({
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

        const libraryId = libraryPagePayload.libraryPayload.id
        if (!libraryId) {
            return
        }

        getLibraryShares(libraryId, userToken).then(response => {

            console.log('getLibraryShares', response.parsedBody)

            if (response.ok) {
                setProps(p => ({
                    ...p,
                    libraryPayload: libraryPagePayload.libraryPayload,
                    libraryShareUserPayloads: response.parsedBody
                }))
            }
        })


    }, [libraryPagePayload])


    const handleChangeNewShare = (shareUsername: string): void => {
        setProps(p => ({
            ...p,
            shareUsername
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
        if (isEmpty(props.shareUsername)) {
            setFieldValidationState(emptyFieldValidation(FieldNames.EMAIL, 'Share with'))
        }

    }


    const dispatch = useDispatch()

    const handleAddShare = (): void => {
        validateForm()

        addLibraryShare({
            email: props.shareUsername || '',
            libraryId: props.libraryPayload.id || 0
        }, userToken).then(response => {
            const parsedBody = response.parsedBody

            if (response.ok && parsedBody) {
                setProps(p => ({
                    ...p,
                    libraryShareUserPayloads: parsedBody.payload
                }))
                dispatch(
                    addNotification({
                        message: 'Library share added.',
                        notificationType: NotificationType.SUCCESS
                    })
                )
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


    const handleDeleteShare = (email: string): void => {
        deleteLibraryShare({
            email,
            libraryId: props.libraryPayload.id || 0
        }, userToken).then(response => {
            const parsedBody = response.parsedBody

            if (response.ok && parsedBody) {
                setProps(p => ({
                    ...p,
                    libraryShareUserPayloads: parsedBody
                }))
                dispatch(
                    addNotification({
                        message: 'Library share deleted.',
                        notificationType: NotificationType.SUCCESS
                    })
                )
            }
        })
    }

    return (
        <div>

            <div>


                <div className="new-line" style={{display: 'flex'}}>
                    <TextField
                        name={FieldNames.EMAIL}
                        label="Share with..."
                        placeholder="Type in the person's email address "
                        value={props.shareUsername || ''}
                        onChange={e => handleChangeNewShare(e.currentTarget.value)}
                        fullWidth={true}
                        error={hasFieldError(FieldNames.EMAIL, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.EMAIL, props.formValidation)}
                    />


                    <Button
                        variant="contained"
                        color="primary"
                        type="button"
                        onClick={handleAddShare}
                    >
                        Add
                    </Button>

                </div>

                <List>

                    {props.libraryShareUserPayloads && props.libraryShareUserPayloads.map(function (libraryShareUserPayload) {
                        return (
                            <ListItem
                                key={libraryShareUserPayload.email}
                                secondaryAction={
                                    <IconButton
                                        edge="end"
                                        aria-label="delete"
                                        onClick={() => handleDeleteShare(libraryShareUserPayload.email)}>
                                        <Delete />
                                    </IconButton>
                                }
                            >
                                <ListItemAvatar>
                                    <Avatar>
                                        {libraryShareUserPayload.validated &&
                                            <Tooltip title="Validated">
                                                <VerifiedUser />
                                            </Tooltip>
                                        }
                                        {!libraryShareUserPayload.validated &&
                                            <Tooltip title="Not yet validated">
                                                <Person />
                                            </Tooltip>
                                        }
                                    </Avatar>
                                </ListItemAvatar>
                                <ListItemText
                                    primary={libraryShareUserPayload.email}
                                    secondary={libraryShareUserPayload.name}
                                />
                            </ListItem>
                        )
                    })}


                    {!props.libraryShareUserPayloads?.length &&
                        <ListItem>
                            <ListItemText>Everyone</ListItemText>
                        </ListItem>
                    }
                </List>
            </div>



        </div>
    )
}

export default LibraryUsers