import { Button, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useHistory } from "react-router-dom";
import ErrorBox from "../components/ErrorBox";
import logo from "../logo.png";
import { useAppDispatch } from "../redux/hooks";
import { RootState } from "../redux/store";
import { logIn, LogInState } from "./features/loggedInUserSlice";


type LogInProps = {
    username: string;
    password: string;
    isInvalidLogIn: boolean
}

const LogIn = () => {

    const dispatch = useAppDispatch();

    const history = useHistory();

    const [props, setProps] = useState<LogInProps>({
        username: '',
        password: '',
        isInvalidLogIn: false
    });


    const setStateValue = (name: string, value: string): void => {
        setProps(p => ({
            ...p,
            [name]: value
        }));
    }

    const logInState = useSelector<RootState, LogInState>(state => state.loggedInUser);

    useEffect(() => {
        if (logInState.currentUser) {
            history.push('/');
        }
    }, [logInState])

    const useHandleSubmit = useCallback((e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()

        dispatch(
            logIn({ username: props.username, password: props.password })
        )

    },
        [props, dispatch]
    )

    return (

        <form onSubmit={useHandleSubmit}>

            <img src={logo} alt="Mashup Media" />

            <h1>Log in</h1>


            {logInState.error &&
                <ErrorBox message={"Invalid username password combination"} />
            }

            <div className="new-line">
                <TextField label="Username" value={props.username} autoComplete="off"
                    onChange={(e) => setStateValue('username', e.currentTarget.value)}
                    fullWidth={true} variant="standard" />
            </div>

            <div className="new-line">
                <TextField name="password" label="Password" value={props.password} autoComplete="off"
                    onChange={(e) => setStateValue('password', e.currentTarget.value)}
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