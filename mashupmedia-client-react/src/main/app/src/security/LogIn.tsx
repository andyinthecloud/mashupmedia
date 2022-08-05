import { Button, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate, useSearchParams } from "react-router-dom";
import logo from "../logo.png";
import { NotificationType, addNotification } from "../notification/notificationSlice";
import { useAppDispatch } from "../redux/hooks";
import type { RootState } from "../redux/store";
import { codeParamName, HttpStatus } from "../utils/httpUtils";
import type { UserLogInPayload } from "./features/loggedInUserSlice";
import { logIn } from "./features/loggedInUserSlice";
import { setTokenCookie } from "./securityUtils";


const LogIn = () => {

    const [searchParams, setSearchParams] = useSearchParams()

    const dispatch = useAppDispatch()


    const code = searchParams.get(codeParamName)
    useEffect(() => {

        if (!searchParams.has(codeParamName)) {
            return
        }

        searchParams.delete(codeParamName)
        setSearchParams(searchParams)

        const httpStatus = Object.values(HttpStatus).find(value => value == code) as HttpStatus

        let message: string

        switch (httpStatus) {
            case HttpStatus.FORBIDDEN:
                message = 'Please log in to renew your session'
                break

            case HttpStatus.SERVER_ERROR:
                message = 'Error contacting Mashup media. Please try again.'
                break

            default:
                message = 'Please log in to renew your session'

        }

        dispatch(
            addNotification({
                message,
                notificationType: NotificationType.WARNING
            })
        )
    }, [code])


    const navigate = useNavigate()



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
        searchParams.delete('code')
        setSearchParams(searchParams)
        dispatch(
            logIn({ username: props.username, password: props.password })
        )
    },
        [dispatch, props]
    )

    useEffect(() => {
        if (logInState.payload) {
            setTokenCookie(logInState.payload.token)
            navigate(-1);
        } else {
            addNotification({
                message: 'Invalid username password combination.',
                notificationType: NotificationType.ERROR
            })

        }
    }, [logInState])



    return (

        <form onSubmit={useHandleSubmit}>

            <img src={logo} alt="Mashup Media" />

            <h1>Log in</h1>

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