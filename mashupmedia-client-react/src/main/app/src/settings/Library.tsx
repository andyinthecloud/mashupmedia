import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { RootState } from "../common/redux/store";
import { fieldErrorMessage, FormValidation, hasFieldError } from "../common/utils/form-validation-utils";
import { getLibrary, LibraryPayload, LibraryTypePayload } from "./backend/libraryCalls";
import { useEffect, useState } from "react";
import { getGroups, NameValuePayload } from "./backend/metaCalls";
import Checkboxes from "../common/components/Checkboxes";
import { toCheckboxPayloads, toNameValuePayloads, toSelectedValues } from "../common/utils/domainUtils";
import { FormControl, FormControlLabel, FormLabel, IconButton, Radio, RadioGroup, TextField } from "@mui/material";
import { Check } from "@mui/icons-material";
import './Library.css'


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


    return (
        <form>
            <h1>Library</h1>

            <div className="new-line">
                <FormControl fullWidth={true}>
                    <FormLabel className='align-left'>Choose library type</FormLabel>
                    <RadioGroup
                        row                        
                        aria-labelledby="demo-radio-buttons-group-label"
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
                    InputProps={{ endAdornment: <IconButton><Check /></IconButton> }}
                />
            </div>

            {props.libraryPayload.createdOn &&
                <div className="new-line">
                    <TextField
                        name={FieldNames.CREATED_ON}
                        label="Created on"
                        value={props.libraryPayload.createdOn}
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
                        error={hasFieldError(FieldNames.PATH, props.formValidation)}
                        helperText={fieldErrorMessage(FieldNames.PATH, props.formValidation)}    
                    />
                </div>
            }


            <div className="new-line">
                <h2>Groups</h2>
                <Checkboxes<number>
                    referenceItems={toCheckboxPayloads<number>(props.groupPayloads)}
                    selectedValues={toSelectedValues<number>(props.libraryPayload.groups)}
                    onChange={handleGroupsChange}
                />
            </div>

        </form>
    )
}

export default Library