import { Alert, AlertColor, AlertTitle } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { clearNotification, NotificationPayload, NotificationType } from "../notification/notificationSlice"



export type AlertBoxPayload = {
    notificationPayloads: NotificationPayload[]
}

const AlertBoxes = (payload: AlertBoxPayload) => {


    const [props, setProps] = useState<AlertBoxPayload>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])

    return (
        <div>
            {props.notificationPayloads.map((notificationPayload) => {
                return (
                    <AlertBox
                        key={notificationPayload.id}
                        id={notificationPayload.id}
                        notificationType={notificationPayload.notificationType}
                        message={notificationPayload.message} />
                )
            })}

        </div>
    )


}

export default AlertBoxes

const AlertBox = (payload: NotificationPayload) => {

    const [props, setProps] = useState<NotificationPayload>({
        ...payload
    })
    useEffect(() => {
        setProps(payload)
    }, [payload])


    const severity = (notificationType: NotificationType | null): AlertColor => {
        const defaultAlertColour: AlertColor = 'info'
        if (!notificationType) {
            return defaultAlertColour
        }

        const alertColor: AlertColor = NotificationType[notificationType].toLowerCase() as AlertColor
        return (alertColor) ? alertColor : defaultAlertColour
    }

    const title = (alertType: NotificationType | null): string => {
        let title: string
        switch (alertType) {
            case NotificationType.ERROR:
                title = 'Error'
                break
            case NotificationType.INFO:
                title = 'Information'
                break
            case NotificationType.SUCCESS:
                title = 'Success'
                break
            case NotificationType.WARNING:
                title = 'Warning'
                break
            default:
                title = 'Warning'
                break

        }
        return title
    }

    const dispatch = useDispatch()

    const handleClose = (notificationId: number | undefined): void => {
        if (notificationId === undefined) {
            return;
        }

        dispatch(clearNotification(notificationId))
        setProps({ ...props })
    }

    return (
        <Alert key={props.id} severity={severity(props.notificationType)} onClose={() => handleClose(props.id)}  >
            <AlertTitle>{title(props.notificationType)}</AlertTitle>
            {props.message}
        </Alert>
    )


}
