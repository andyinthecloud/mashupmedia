import { Button, FormControlLabel, FormGroup, Switch, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { PayloadAction } from "../common/redux/actions";
import { useAppDispatch } from "../common/redux/hooks";
import type { RootState, SecurePayload } from "../common/redux/store";

import { addNotification, NotificationType } from "../common/notification/notificationSlice";
import { HttpStatus, redirectLogin } from "../common/utils/httpUtils";
import type { NetworkProxyPayload } from "./features/networkSlice";
import { postNetworkProxy } from "./features/networkSlice";


const NetworkForm = () => {

    const securityPayload = useSelector((state: RootState) => state.security.payload)

    const dispatch = useAppDispatch()

    const networkProxyPayloadState = useSelector((state: RootState) => state.networkProxy)

    const [props, setProps] = useState<NetworkProxyPayload>({
        enabled: true,
        url: '',
        port: 0,
        username: '',
        password: ''
    })



    useEffect(() => {
        if (networkProxyPayloadState.error) {
            redirectLogin(HttpStatus.FORBIDDEN)
            return
        }

        setProps(p => ({
            ...p,
            ...networkProxyPayloadState.payload
        }))

    }, [networkProxyPayloadState])

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    function isFormDisabled(): boolean {
        return props.enabled ? false : true
    }

    const useHandleSubmit = useCallback((e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const securePayload: SecurePayload<NetworkProxyPayload> = { userToken: securityPayload?.token, payload: props }
        dispatch(
            postNetworkProxy(securePayload)
        )
    },
        [dispatch, props, securityPayload]
    )

    useEffect(() => {

        if (networkProxyPayloadState.payloadAction === PayloadAction.SAVED) {
            addNotification({
                message: 'Account saved',
                notificationType: NotificationType.SUCCESS
            })            
        } else {
            addNotification({
                message: 'Error saving account',
                notificationType: NotificationType.ERROR
            })
        }
    },
        [networkProxyPayloadState]
    )

    return (

        <form onSubmit={useHandleSubmit}>

            <h1>Network</h1>

            <FormGroup className="new-line">
                <FormControlLabel
                    control={
                        <Switch
                            name="useProxy"
                            checked={props.enabled}
                            onClick={e => setStateValue('enabled', !props.enabled)}
                            color="primary"
                        />
                    }
                    label="Enable proxy"
                />
            </FormGroup>

            <div className="new-line">
                <TextField name="url" label="URL" value={props.url} onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    disabled={isFormDisabled()} fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField name="port" label="Port" value={props.port} type="number" onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    disabled={isFormDisabled()} fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField name="username" label="Username" value={props.username}
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)} disabled={isFormDisabled()} fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField name="password" label="Password" value={props.password} type="password"
                    onChange={e => setStateValue(e.currentTarget.name, e.currentTarget.value)} disabled={isFormDisabled()} fullWidth={true} />
            </div>

            <div className="new-line">
                <Button variant="outlined" type="submit">
                    Save
                </Button>
            </div>

        </form>
    )
}


export default NetworkForm