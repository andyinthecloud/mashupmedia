import { Box, Button, Checkbox, FormControlLabel, FormGroup, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import AlertBox, { AlertBoxType } from "../components/AlertBox"
import Checkboxes from "../components/Checkboxes"
import type { RootState } from "../redux/store"
import { displayDateTime } from "../utils/dateUtils"
import { toCheckboxPayloads, toNameValuePayloads, toSelectedValues } from "../utils/domainUtils"
import { HttpStatus, redirectLogin } from "../utils/httpUtils"
import { fetchGroupPayloads, fetchRolePayloads, NameValuePayload } from "./backend/metaCalls"
import { fetchMyAccount, saveMyAccount, UserPayload } from "./backend/userCalls"

const MyAccount = () => {

    
    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    const [props, setProps] = useState<UserPayload>({
            enabled: false,
            editable: false,
            name: '',
            username: ''
    
    })

    useEffect(() => {
        fetchMyAccount(userToken)
        .then((response) => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
            }
        })
        .catch(error => redirectLogin(HttpStatus.FORBIDDEN))

    }, [userToken])

    const [groupPayloads, setGroupPayloads] = useState<NameValuePayload<number>[]>([])
    const [rolePayloads, setRolePayloads] = useState<NameValuePayload<string>[]>([])

    useEffect(() => {

        fetchGroupPayloads(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setGroupPayloads(response.parsedBody)
            }
        })

        fetchRolePayloads(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setRolePayloads(response.parsedBody)
            }
        })

    }, [])


    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    const isEditable = (): boolean => (!props.editable)

    const handleRolesChange = (values: string[]) => {
        setStateValue('rolePayloads', toNameValuePayloads(values))
    }

    const handleGroupsChange = (values: number[]) => {
        setStateValue('groupPayloads', toNameValuePayloads(values))
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        saveMyAccount(props, userToken)
        .then(result => {
            setSuccessfulSave(true)
        })
        .catch(error => setSuccessfulSave(false))
    }

    const navigate = useNavigate()
    function handleCancel(): void {
        navigate('/')
    }

    function handleChangeUserPassword(): void {
        navigate(`/settings/change-user-password/${props.username}`)
    }

    const [isSuccessfulSave, setSuccessfulSave] = useState(false)


    return (
        <form onSubmit={handleSubmit}>
            <h1>Edit user</h1>

            <AlertBox alertType={AlertBoxType.SUCCESS} message="Account saved." isShow={isSuccessfulSave}></AlertBox>


            <div className="new-line">
                <Box sx={{ color: 'primary.main' }}>
                    <FormGroup>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    disabled={isEditable()}
                                    name="enabled"
                                    checked={props.enabled}
                                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.checked)}
                                />}
                            label="Enabled" />
                    </FormGroup>
                </Box>
            </div>

            <div className="new-line">
                <TextField
                    name="username"
                    label="Username"
                    disabled={isEditable()}
                    value={props.username}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField
                    name="name"
                    label="Name"
                    disabled={isEditable()}
                    value={props.name}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true} />
            </div>

            {props.createdOn &&
                <div className="new-line">
                    <TextField
                        label="Created on"
                        disabled={true}
                        value={displayDateTime(props.createdOn)}
                        fullWidth={true} />
                </div>
            }

            {props.updatedOn &&
                <div className="new-line">
                    <TextField
                        label="Updated on"
                        disabled={true}
                        value={displayDateTime(props.updatedOn)}
                        fullWidth={true} />
                </div>
            }

            <div className="new-line">
                <h2>Roles</h2>
                <Checkboxes<string>
                    isDisabled={!props.editable}
                    referenceItems={toCheckboxPayloads<string>(rolePayloads)}
                    selectedValues={toSelectedValues<string>(props.rolePayloads)}
                    onChange={handleRolesChange}
                />
            </div>

            <div className="new-line">
                <h2>Groups</h2>
                <Checkboxes<number>
                    isDisabled={!props.editable}
                    referenceItems={toCheckboxPayloads<number>(groupPayloads)}
                    selectedValues={toSelectedValues<number>(props.groupPayloads)}
                    onChange={handleGroupsChange}
                />
            </div>

            <div className="new-line right">
                <Button variant="contained" color="secondary"  type="button" onClick={handleCancel}>
                    Cancel
                </Button>
                <Button variant="contained" color="secondary" type="button" onClick={handleChangeUserPassword}>
                    Change password
                </Button>

                <Button variant="contained" color="primary" type="submit" disabled={!props.editable}>
                    Save
                </Button>
            </div>

        </form>
    )



}


export default MyAccount