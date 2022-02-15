import { Alert, AlertTitle, Button, FormControlLabel, FormGroup, Switch, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useHistory } from "react-router-dom";
import { useAppDispatch } from "../redux/hooks";
import { PayloadAction, RootState, SecurePayload } from "../redux/store";
import { getNetworkProxy, NetworkProxyPayload, postNetworkProxy } from "./features/networkSlice";


const NetworkForm = () => {

    const userToken = useSelector((state: RootState) => state.loggedInUser.payload?.token)
    const dispatch = useAppDispatch()

    useEffect(() => {
        dispatch(
            getNetworkProxy(userToken)
        )

    }, [dispatch, userToken])

    const networkPayload = useSelector((state: RootState) => state.networkProxy.payload)
    const networkPayloadAction = useSelector((state: RootState) => state.networkProxy.payloadAction)

    const [props, setProps] = useState<NetworkProxyPayload>({
        enabled: true,
        url: '',
        port: 0,
        username: '',
        password: ''
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            ...networkPayload
        }))

    }, [networkPayload])

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    function isFormDisabled(): boolean {
        return props.enabled ? false : true
    }

    const history = useHistory()

    const useHandleSubmit = useCallback((e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const securePayload: SecurePayload<NetworkProxyPayload> = { userToken, payload: props }
        console.log('useHandleSubmit', securePayload)
        dispatch(
            postNetworkProxy(securePayload)
        )
    },
        [dispatch, props, userToken]
    )

    let isSuccessfulSave = false

    useEffect(() => {
        if (networkPayloadAction === PayloadAction.SAVED) {
            isSuccessfulSave = true
        } else {
            isSuccessfulSave = false
        }

    },
        [networkPayloadAction, isSuccessfulSave]
    )




    return (

        <form onSubmit={useHandleSubmit}>


            <Alert severity="success">
                <AlertTitle>Success</AlertTitle>
                This is a success alert — <strong>check it out!</strong>
            </Alert>

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

            <pre>{JSON.stringify(props)}</pre>

        </form>
    )
}


export default NetworkForm