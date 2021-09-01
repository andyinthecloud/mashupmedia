import React from "react";
import {Button, FormControlLabel, FormGroup, Switch, TextField} from "@material-ui/core";


class NetworkForm extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            useProxy: false,
            url: "",
            port: "",
            username: "",
            password: ""
        };
        // useSt
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSwitch = this.handleSwitch.bind(this);

        console.log(this.state);

        console.log("process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL: " + process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL);
        console.log("process.env.NODE_ENV: " + process.env.NODE_ENV);
    }

    handleInputChange(event: any) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
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
                "Content-type": "application/json; charset=UTF-8"
            }});
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

                <div>
                    <TextField name="url" label="URL" value={this.state.url} onChange={this.handleInputChange}
                               disabled={this.isFormDisabled()} fullWidth={true}/>
                </div>

                <div>
                    <TextField name="port" label="Port" value={this.state.port} onChange={this.handleInputChange}
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

                <Button type="submit" color="primary">
                    Save
                </Button>

            </form>
        );
    }
}


export default NetworkForm