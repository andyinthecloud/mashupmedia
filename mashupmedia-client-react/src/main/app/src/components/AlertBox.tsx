import { Alert, AlertTitle } from "@mui/material"


enum AlertBoxType {
    ERRROR, WARNING, INFO, SUCCESS
}

export type AlertBoxProperties = {
    message: string;
    alertType: AlertBoxType
}

const AlertBox = (props: AlertBoxProperties) => {
    // {logInState.error &&

    return (
    
    <Alert severity="success">
        <AlertTitle>Success</AlertTitle>
        This is a success alert — <strong>check it out!</strong>
    </Alert>
    )


}

export default AlertBox