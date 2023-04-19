import { AnyAction } from "@reduxjs/toolkit";
import { NotificationType, addNotification } from "../../../common/notification/notificationSlice"
import { PlaylistActionStatusTypePayload } from "./playlistCalls"


export const playlistNotification = (playlistActionStatusTypePayload?: PlaylistActionStatusTypePayload): AnyAction => {


    switch (playlistActionStatusTypePayload) {
        case PlaylistActionStatusTypePayload.FFMPEG_NOT_INSTALLED:
            return addNotification({
                message: "FFmpeg is not installed. Please configure configure it in the left menu, Settings -> Encoding",
                notificationType: NotificationType.WARNING
            })

        case PlaylistActionStatusTypePayload.ITEMS_SENT_FOR_ENCODING:
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

