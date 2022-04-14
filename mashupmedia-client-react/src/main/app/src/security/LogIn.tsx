import { Button, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import AlertBox, { AlertBoxType } from "../components/AlertBox";
import logo from "../logo.png";
import { useAppDispatch } from "../redux/hooks";
import type { RootState } from "../redux/store";
import { logIn } from "./features/loggedInUserSlice";
import type { UserLogInPayload } from "./features/loggedInUserSlice";


const LogIn = () => {

    const dispatch = useAppDispatch();

    const navigate = useNavigate();

    const [props, setProps] = useState<UserLogInPayload>({
        username: '',
        password: ''
    });

    const setStateValue = (name: string, value: string): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }))
    }

    const logInState = useSelector((state: RootState) => state.loggedInUser)


    const useHandleSubmit = useCallback((e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        dispatch(
            logIn({ username: props.username, password: props.password })
        )

    },
        [dispatch, props]
    )

    useEffect(() => {
        if (logInState.payload) {
            navigate('/');
        }
    })

    return (

        <form onSubmit={useHandleSubmit}>

            <img src={logo} alt="Mashup Media" />

            <h1>Log in</h1>


            <AlertBox alertType={AlertBoxType.ERRROR} message="Invalid username password combination." isShow={logInState.error ? true : false}></AlertBox>

            <div className="new-line">
                <TextField label="Username" value={props.username} autoComplete="off"
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    name="username" fullWidth={true} variant="standard" />
            </div>

            <div className="new-line">
                <TextField name="password" label="Password" value={props.password} autoComplete="off"
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true} type={"password"} variant="standard" />
            </div>

            <div className="new-line">
                <Button variant="outlined" type="submit">
                    Log in
                </Button>
            </div>



            <pre>Form</pre>
            <pre>{JSON.stringify(props)}</pre>


            <pre>Logged in user</pre>
            <pre>{JSON.stringify(logInState)}</pre>

        </form>


    )
}

export default LogIn