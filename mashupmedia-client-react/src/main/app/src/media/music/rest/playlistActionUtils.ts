import { AnyAction } from "@reduxjs/toolkit";
import { NotificationType, addNotification } from "../../../common/notification/notificationSlice"
import { EncoderStatusType } from "./playlistCalls"


export const playlistNotification = (playlistActionStatusTypePayload?: EncoderStatusType): AnyAction => {


    switch (playlistActionStatusTypePayload) {
        case EncoderStatusType.ENODER_NOT_INSTALLED:
            return addNotification({
                message: "FFmpeg is not installed. Please configure configure it in the left menu, Settings -> Encoding",
                notificationType: NotificationType.WARNING
            })

        case EncoderStatusType.SENT_FOR_ENCODING:
            return addNotification({
                message: "Some tracks have queued for encoding because they are not compatible for web streaming. They will be available soon.",
                notificationType: NotificationType.INFO
            })

        default:
            return addNotification({
                message: "Added to playlist",
                notificationType: NotificationType.SUCCESS
            })
    }

}

