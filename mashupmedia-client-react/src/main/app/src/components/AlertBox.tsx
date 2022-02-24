import { Alert, AlertColor, AlertTitle } from "@mui/material"


export enum AlertBoxType {
    ERRROR = 'error', WARNING = 'warning', INFO = 'info', SUCCESS = 'success'
}

export type AlertBoxProperties = {
    message: string;
    alertType: AlertBoxType
    isShow: boolean
}

const AlertBox = (props: AlertBoxProperties) => {

    const severity = (alertType: AlertBoxType): AlertColor => {
        const alertColor: AlertColor = alertType.toString().toLowerCase() as AlertColor
        const defaultAlertColour: AlertColor = 'info'
        return (alertColor) ? alertColor : defaultAlertColour
    }

    const title = (alertType: AlertBoxType): string => {
        let title: string
        switch (alertType) {
            case AlertBoxType.ERRROR:
                title = 'Error'
                break
            case AlertBoxType.INFO:
                title = 'Information'
                break
            case AlertBoxType.SUCCESS:
                title = 'Success'
                break
            case AlertBoxType.WARNING:
                title = 'Warning'
                break
            default:
                title = 'Warning'
                break

        }
        return title
    }


    return (
        <span>
            {props.isShow &&
                <Alert severity={severity(props.alertType)} >
                    <AlertTitle>{title(props.alertType)}</AlertTitle>
                    {props.message}
                </Alert>
            }
        </span>
    )


}

export default AlertBox