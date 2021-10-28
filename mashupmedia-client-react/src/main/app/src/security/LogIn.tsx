import logo from "../logo.png";
import React, {FormEvent} from "react";
import TextField from '@mui/material/TextField';
import {getNameValueFromEvent, NameValue} from "../utils/FormUtils";
import {Button} from "@mui/material";
import {ErrorBox} from "../components/ErrorBox";
import {useAppDispatch, useAppSelector} from "../redux/hooks";
import { useState } from 'react';
import { decrement, increment } from './features/counterSlice';
import {RootStateOrAny} from "react-redux";
import {RootState} from "../redux/store";

class LogInForm extends React.Component<any, any> {

    private isInvalidLogin: boolean = false;


    constructor(props: any) {
        super(props);
        this.state = {
            username: "",
            password: ""
        }

        this.handleInputChange = this.handleInputChange.bind(this);


        const count = useAppSelector((state)) => state.counter.value;
        const dispatch = useAppDispatch();
    }

    handleInputChange(event: any) {
        const nameValue: NameValue = getNameValueFromEvent(event);
        this.setState({
            [nameValue.name]: nameValue.value
        })
    }


    invalidLoginMessage = () => {
        return
        if (this.isInvalidLogin) {
            <div>Invalid login</div>
        }
    }

    handleSubmit = (event: React.FormEvent) => {
        // perform_login

        event.preventDefault();

        const loginUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/login';
        // console.log('process: ', process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL);

        // const formUrl = process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL?.toString();

        const formData = new FormData();
        formData.append('username', this.state.username);
        formData.append('password', this.state.password);


        const response = fetch(loginUrl, {
            method: 'POST',
            mode: 'cors',
            credentials: 'omit',
            headers: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS'
            },
            body: JSON.stringify(this.state),
        })
            .then(response => response.json());

        console.log('response', response);

    }


    render() {
        return (

            <form onSubmit={this.handleSubmit}>

                <img src={logo} alt="Mashup Media"/>

                <div>Log in</div>

                {this.state.isInvalidLogin && <ErrorBox message={"Invalid username password combination"}/>}


                <div className="new-line">

                    <TextField name="username" label="Username" value={this.state.username}
                               onChange={this.handleInputChange}
                               fullWidth={true} variant={"standard"}/>

                </div>

                <div className="new-line">
                    <TextField name="password" label="Password" value={this.state.password}
                               onChange={this.handleInputChange}
                               fullWidth={true} type={"password"} variant="standard"/>
                </div>

                <div className="new-line">


                    <Button variant="outlined" type="submit">
                        Log in
                    </Button>
                </div>

            </form>

        )
    }
}

export default LogInForm;