import React from "react";
import {Button, FormControlLabel, FormGroup, Switch, TextField} from "@mui/material";
import {getNameValueFromEvent, NameValue} from "../utils/FormUtils";


// interface Network {
//     useProxy: boolean,
//     url: string,
//     port: number,
//     username: string,
//     password: string;
// }

class NetworkForm extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            useProxy: false,
            url: "",
            port: 0,
            username: "",
            password: ""
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSwitch = this.handleSwitch.bind(this);

        console.log(this.state);

        console.log("process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL: " + process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL);
        console.log("process.env.NODE_ENV: " + process.env.NODE_ENV);
    }

    handleInputChange(event: any) {
        const nameValue: NameValue = getNameValueFromEvent(event);
        this.setState({
            [nameValue.name]: nameValue.value
        })
    }

    handleSwitch(event: any) {
        this.setState({value: event.target.value});
    }


    handleSubmit = (event:any)  => {
        event.preventDefault();
        console.log('on submit');
        console.log(JSON.stringify(this.state));


        const response = fetch("{process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL} ", {
            method: 'POST',
            body: JSON.stringify(this.state),
            headers: {
                // "Content-type": "application/json; charset=UTF-8"

                'X-XSRF-TOKEN': this.getCookie('XSRF-TOKEN'),
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }});
    }

    getCookie(cookieName: string) :string {
        // let cookieValue = document.cookie.replace(/(?:(?:^|.*;\s*)username\s*\=\s*([^;]*).*$)|^.*$/, "$1");
        const values = document.cookie.match('(^|;)\\s*' + cookieName + '\\s*=\\s*([^;]+)');
        const value = values ? String(values.pop()) : '';
        console.log('cookie value = ', value);
        return value;
    }

    isFormDisabled() {
        return this.state.useProxy ? false : true;
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <h1>Network</h1>
                %REACT_APP_MASHUPMEDIA_BACKEND_URL%
                <br/>
                %NODE_ENV%

                <FormGroup>
                    <FormControlLabel
                        control={
                            <Switch
                                checked={this.state.useProxy}
                                onChange={this.handleInputChange}
                                name="useProxy"
                                color="primary"
                            />
                        }
                        label="Enable proxy"

                    />

                </FormGroup>

                <div className="new-line">
                    <TextField name="url" label="URL" value={this.state.url} onChange={this.handleInputChange}
                               disabled={this.isFormDisabled()} fullWidth={true}/>
                </div>

                <div>
                    <TextField name="port" label="Port" value={this.state.port} type="number" onChange={this.handleInputChange}
                               disabled={this.isFormDisabled()} fullWidth={true}/>
                </div>

                <div>
                    <TextField name="username" label="Username" value={this.state.username}
                               onChange={this.handleInputChange} disabled={this.isFormDisabled()} fullWidth={true}/>
                </div>

                <div>
                    <TextField name="password" label="Password" value={this.state.password}
                               onChange={this.handleInputChange} disabled={this.isFormDisabled()} fullWidth={true}/>
                </div>

                <Button type="submit" className="mashup-button">
                    Save
                </Button>

            </form>
        );
    }
}


export default NetworkForm