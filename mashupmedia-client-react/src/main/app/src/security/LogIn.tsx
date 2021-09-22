import logo from "../logo.png";
import React from "react";
import TextField from '@mui/material/TextField';
import {getNameValueFromEvent, NameValue} from "../utils/FormUtils";
import {Button} from "@mui/material";

class LogInForm extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            username: "",
            password: ""
        }

        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange(event: any) {
        const nameValue: NameValue = getNameValueFromEvent(event);
        this.setState({
            [nameValue.name]: nameValue.value
        })
    }

    render() {
        return (
            <form>

            <img src={logo} alt="Mashup Media"/>

            <div>Log in</div>

            <form>
                <div className="new-line">
                    <TextField name="username" label="Username" value={this.state.username} onChange={this.handleInputChange}
                                fullWidth={true} variant={"standard"}/>
                </div>

                <div className="new-line">
                    <TextField name="password" label="Password" value={this.state.password} onChange={this.handleInputChange}
                               fullWidth={true} type={"password"} variant="standard" />
                </div>

                <div className="new-line">
                <Button  variant="outlined">
                    Log in
                </Button>
                </div>
            </form>

        </form>
        )
    }
}

export default LogInForm;