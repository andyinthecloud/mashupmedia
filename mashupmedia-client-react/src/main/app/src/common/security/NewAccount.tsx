import { Button, TextField } from "@mui/material"
import logo from "../../logo.png"
import { UserPayload } from "../../configuration/backend/userCalls"
import { useEffect, useState } from "react"



type NewAccountPayload = {
    userPayload: UserPayload
}


const NewAccount = () => {

    const [props, setProps] = useState<NewAccountPayload>()


    return (
        <form className="zero-top-margin">

            <img src={logo} className="logo" alt="Mashup Media" />

            <h1>New account</h1>

            <div className="new-line">
                <TextField label="Username" value={props?.userPayload.username} autoComplete="off"
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    name="username" fullWidth={true} variant="standard" />
            </div>

            <div className="new-line">
                <TextField name="password" label="Password" value={props.userPayload.password} autoComplete="off"
                    onChange={(e) => setStateValue(e.currentTarget.name, e.currentTarget.value)}
                    fullWidth={true} type={"password"} variant="standard" />
            </div>

            <div className="new-line">
                <Button variant="outlined" type="submit">
                    Sign up
                </Button>
            </div>
        </form>
    )
}

export default NewAccount