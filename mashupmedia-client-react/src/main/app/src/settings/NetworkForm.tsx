import { Button, FormControlLabel, FormGroup, Switch, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useAppDispatch } from "../redux/hooks";
import { getNetworkProxy, NetworkProxyPayload } from "./features/networkSlice";


const NetworkForm = () => {

    const dispatch = useAppDispatch();

    const getProxy = useCallback(() => {
        console.log('useCallback proxy')
        dispatch(
            getNetworkProxy()
        )
    
    }, [dispatch])



    const [props, setProps] = useState<NetworkProxyPayload>({
        enabled: true,
        url: '',
        port: 0,
        username: '',
        password: '',
    })


    useEffect(() => {
        // getProxy()

        console.log('useCallback proxy')
        dispatch(
            getNetworkProxy()
        )
    })

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    function isFormDisabled(): boolean {
        return props.enabled ? false : true
    }

    return (
        <form >
            <h1>Network</h1>

            <FormGroup className="new-line">
                <FormControlLabel
                    control={
                        <Switch
                            name="useProxy"
                            checked={props.enabled}
                            onClick={e => setStateValue('useProxy', !props.enabled)}
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