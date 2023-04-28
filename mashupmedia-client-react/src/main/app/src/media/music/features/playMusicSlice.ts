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
            state.currentTrackId = undefined
            state.requestPlaylistTrackId = undefined
        },
        playingTrackId(state, action: PayloadAction<number>) {
            state.currentTrackId = action.payload
            state.requestPlaylistTrackId = undefined
        },
        requestPlaylistTrackId(state, action: PayloadAction<number>) {
            state.triggerPlay = timestamp()
            state.currentTrackId = undefined
            state.requestPlaylistTrackId = action.payload
        }
    }
})

export const { play, playingTrackId, requestPlaylistTrackId} = playMusicSlice.actions

export default playMusicSlice