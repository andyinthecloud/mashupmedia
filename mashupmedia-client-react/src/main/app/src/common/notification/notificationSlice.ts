import { createSlice, PayloadAction } from '@reduxjs/toolkit';


export enum NotificationType {
    ERROR, WARNING, INFO, SUCCESS
}


// Define a type for the slice state
export interface NotificationPayload {
    id?: number
    message: string | null
    notificationType: NotificationType | null
}

export interface NotificationPayloads {
    notificationPayloads: NotificationPayload[]
}

const initialState: NotificationPayloads = {
    notificationPayloads: []
}


export const notificationSlice = createSlice({
    name: 'notification',
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    reducers: {
        addNotification: (state, action: PayloadAction<NotificationPayload>) => {
            const notificationPayload: NotificationPayload = {
                ...action.payload,
                id: Date.now()
            };

            state.notificationPayloads.push(notificationPayload)
        },
        clearNotification: (state, action: PayloadAction<number>) => {

            state.notificationPayloads.map((notificationPayload, index) => {
                if (notificationPayload.id === action.payload) {
                    state.notificationPayloads.splice(index, 1)        
                }
            })
        },
        clearAllNotifications: (state) => {
            state.notificationPayloads = []
        },

    },
})

export const { addNotification, clearNotification, clearAllNotifications } = notificationSlice.actions

// Other code such as selectors can use the imported `RootState` type
// export const selectCount = (state: RootState) => state.counter.value

export default notificationSlice