import { Box, Button, Checkbox, FormControlLabel, FormGroup, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import LineItems, { LineItemPayload } from "../components/LineItems"
import { useAppDispatch } from "../redux/hooks"
import type { RootState } from "../redux/store"
import { displayDateTime } from "../utils/dateUtils"
import { fetchGroupPayloads, fetchRolePayloads, GroupPayload, RolePayload } from "./ajax/metaCalls"
import { getMyAccount, UserPayload } from "./features/userSlice"

const MyAccount = () => {

    const dispatch = useAppDispatch()
    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)

    useEffect(() => {
        dispatch(
            getMyAccount(userToken)
        )
    }, [userToken, dispatch])

    const userPayload = useSelector((state: RootState) => state.user.payload)
    // const userPayloadAction = useSelector((state: RootState) => state.user.payloadAction)

    const [props, setProps] = useState<UserPayload>({
        admin: false,
        enabled: false,
        editable: false,
        name: '',
        username: ''

    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            ...userPayload
        }))

    }, [userPayload])


    const [groupPayloads, setGroupPayloads] = useState<GroupPayload[]>([])
    const [rolePayloads, setRolePayloads] = useState<RolePayload[]>([])

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

    const toLineItemPayloadArrayFromRolePayload = (rolePayloadArray: RolePayload[] | undefined): LineItemPayload[] => {


        const lineItemPayloadArray: LineItemPayload[] = []

        if (rolePayloadArray === undefined || !rolePayloadArray.length) {
            return lineItemPayloadArray;
        }

        rolePayloadArray.forEach(rolePayload => {
            lineItemPayloadArray.push({
                id: rolePayload.idName,
                name: rolePayload.name
            })
        });

        return lineItemPayloadArray;



    }

    return (
        <form>
            <h1>Edit user</h1>


            <div className="new-line">
                <Box sx={{ color: 'primary.main' }}>
                    <FormGroup>
                        <FormControlLabel
                            control={
                                <Checkbox
                                    disabled={isEditable()}
                                    name="admin"
                                    checked={props.admin}
                                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.checked)}
                                />}
                            label="Administrator" />
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
                <LineItems isDisabled={props.admin} lineItemPayloads={toLineItemPayloadArrayFromRolePayload(props.rolePayloads)}></LineItems>
            </div>

            <div className="new-line">
                <Button variant="outlined" type="button" style={{ marginRight: "1em" }}>
                    Cancel
                </Button>

                <Button variant="outlined" type="submit">
                    Save
                </Button>
            </div>


            <pre> {JSON.stringify(props)}</pre>

            <pre> {JSON.stringify(groupPayloads)}</pre>
            <pre> {JSON.stringify(rolePayloads)}</pre>

        </form>
    )



}


export default MyAccount