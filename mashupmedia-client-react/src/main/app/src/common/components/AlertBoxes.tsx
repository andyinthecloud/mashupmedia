import { Alert, AlertColor } from "@mui/material"
import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { clearNotification, NotificationPayload, NotificationType } from "../../common/notification/notificationSlice"
import { RootState } from "../../common/redux/store"

export type AlertBoxPayload = {
    notificationPayloads: NotificationPayload[]
}


const AlertBoxes = () => {

    const notificationPayloadsState = useSelector((state: RootState) => state.notification)

    const [props, setProps] = useState<AlertBoxPayload>()


    useEffect(() => {
        setProps(notificationPayloadsState)
    }, [notificationPayloadsState])

    return (
        <div>
            {props?.notificationPayloads.map((notificationPayload) => {
                return (
                    <AlertBox
                        key={notificationPayload.id}
                        id={notificationPayload.id}
                        notificationType={notificationPayload.notificationType}
                        message={notificationPayload.message}
                    />
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

    const [fade, setFade] = useState<boolean>(false)

    useEffect(() => {
        setProps(payload)
        setTimeout(() => {
            dispatch(
                clearNotification(payload.id || 0)
            )
        }, 5000)
    }, [payload])


    const severity = (notificationType: NotificationType | null): AlertColor => {
        const defaultAlertColour: AlertColor = 'info'
        if (!notificationType) {
            return defaultAlertColour
        }

        const alertColor: AlertColor = NotificationType[notificationType].toLowerCase() as AlertColor
        return (alertColor) ? alertColor : defaultAlertColour
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
        <Alert
            key={props.id}
            severity={severity(props.notificationType)}
            onClose={() => handleClose(props.id)}
            sx={{
                marginBottom: 5
            }}

        >
            {props.message}
        </Alert>
    )

}
