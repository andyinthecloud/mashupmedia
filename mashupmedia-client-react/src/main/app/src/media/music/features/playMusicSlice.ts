import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { timestamp } from "../../../common/utils/httpUtils";

export type PlayMusicPayload = {
    triggerPlay?: number
    currentTrackId?: number
    requestPlaylistTrackId?: number
}

const initialState: PlayMusicPayload = ({})

const playMusicSlice = createSlice({
    name: 'media/music/play',
    initialState,
    reducers: {
        play(state) {
            state.triggerPlay = timestamp()
        },
        playingTrackId(state, action: PayloadAction<number>) {
            state.currentTrackId = action.payload
        },
        requestPlaylistTrackId(state, action: PayloadAction<number>) {
            state.requestPlaylistTrackId = action.payload
            state.triggerPlay = timestamp()
        }
    }
})

export const { play, playingTrackId, requestPlaylistTrackId} = playMusicSlice.actions

export default playMusicSlice