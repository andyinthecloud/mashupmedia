import { useEffect, useState } from "react"
import { LibraryPayload, LibraryTypePayload, addLibraryShare } from "../backend/libraryCalls"
import { LibraryPagePayload, TabPanelPayload } from "./Library"
import { Button, List, TextField } from "@mui/material"
import { FieldValidation, FormValidation, emptyFieldValidation, fieldErrorMessage, hasFieldError, isEmpty, toFieldValidation } from "../../common/utils/formValidationUtils"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { useDispatch } from "react-redux"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"


type LibrayUsersPayload = {
    libraryPayload: LibraryPayload
    formValidation: FormValidation
    tabPanelPayload: TabPanelPayload
    shareUsername?: string
}

const LibraryUsers = (libraryPagePayload: LibraryPagePayload) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const tabIndex = 2;

    const enum FieldNames {
        EMAIL = 'email'
    }

    const [props, setProps] = useState<LibrayUsersPayload>({
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

    const handleAddNewShare = (): void => {
        validateForm()

        addLibraryShare({
            email: props.shareUsername || '',
            libraryId: props.libraryPayload.id || 0
        }, userToken).then(response => {
            const parsedBody = response.parsedBody

            if (response.ok && parsedBody) {
                setProps(p => ({
                    ...p,
                    libraryPayload: parsedBody?.payload
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

    return (
        <div
            hidden={props.tabPanelPayload.value !== props.tabPanelPayload?.index}
        >


            <div className="new-line">
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
            </div>

            <div className="new-line right">

                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                    onClick={handleAddNewShare}
                >
                    Add
                </Button>

            </div>

            <List>


            </List>


        </div>
    )
}

export default LibraryUsers