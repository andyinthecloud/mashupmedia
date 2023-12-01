import { Button, TextField } from "@mui/material";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate, useSearchParams } from "react-router-dom";
import logo from "../../logo.png";
import { addNotification, NotificationType } from "../notification/notificationSlice";
import { useAppDispatch } from "../redux/hooks";
import { RootState } from "../redux/store";
import { codeParamName, HttpStatus, jumpUriParamName } from "../utils/httpUtils";
import { logIn, UserLogInPayload } from "./features/securitySlice";
import { setTokenCookie } from "./securityUtils";


const LogIn = () => {

    const [searchParams, setSearchParams] = useSearchParams()

    const dispatch = useAppDispatch()


    const code = searchParams.get(codeParamName)
    const encodedJumpUri = searchParams.get(jumpUriParamName)

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

    const securityState = useSelector((state: RootState) => state.security)

    const handleSubmit = useCallback((e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        searchParams.delete(codeParamName)
        setSearchParams(searchParams)
        dispatch(
            logIn({ username: props.username, password: props.password })
        )
    },
        [dispatch, props]
    )

    const handleResetPassword = () => {
        console.log('handleResetPassword')
        navigate('/reset-password')
    }


    useEffect(() => {
        if (securityState.error?.length) {
            dispatch(
                addNotification({
                    message: 'Invalid username password combination.',
                    notificationType: NotificationType.ERROR
                })
            )
        }

        if (securityState.payload) {
            setTokenCookie(securityState.payload?.token)
            const navigateUri = encodedJumpUri ? decodeURI(encodedJumpUri) : '/'
            navigate(navigateUri)
        }

    }, [securityState])


    return (

        <form onSubmit={handleSubmit} className="zero-top-margin">

            <img src={logo} className="logo" alt="Mashup Media" />

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

            <div className="new-line right">
                <Button variant="text" color="secondary" type="button" onClick={handleResetPassword}>
                    Reset password
                </Button>

                <Button variant="contained" color="primary" type="submit">
                    Log in
                </Button>
            </div>

        </form>


    )
}

export default LogIn