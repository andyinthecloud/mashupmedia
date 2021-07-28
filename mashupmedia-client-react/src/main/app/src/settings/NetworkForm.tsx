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
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSwitch = this.handleSwitch.bind(this);
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

    handleSubmit(event: any) {
        console.log(this.state);
        event.preventDefault();
        return false;
    }

    isFormDisabled() {
        return this.state.useProxy ? false : true;
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <h1>Network</h1>

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