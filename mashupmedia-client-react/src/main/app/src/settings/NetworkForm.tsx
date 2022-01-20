import { Button, FormControlLabel, FormGroup, Switch, TextField, UseSwitchProps } from "@mui/material";
import React, { useState } from "react";

type NetworkFormProps = {
    useProxy: boolean
    url: string
    port: number
    username: string
    password: string
}


const NetworkForm = () => {

    const [props, setProps] = useState<NetworkFormProps>({
        useProxy: true,
        url: '',
        port: 0,
        username: '',
        password: '',
    });

    const switchProps: UseSwitchProps = {
        checked: true
    }

    const setStateValue = (name: string, value: any): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    function isFormDisabled(): boolean {
        return props.useProxy ? false : true;
    }

    return (
        <form >
            <h1>Network</h1>

            <FormGroup className="new-line">
                <FormControlLabel
                    control={
                        <Switch
                            name="useProxy"
                            checked={props.useProxy}
                            onClick={e => setStateValue('useProxy', !props.useProxy)}
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