import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { timestamp } from "../../../common/utils/httpUtils";


export type LoadPlayMusicPayload = {
    loadPlaylistMediaItemId?: number 
    loadPlaylistId?: number
}

export type PlayMusicPayload = {
    triggerPlay?: number
    loadPlaylistMediaItemId?: number 
    loadPlaylistId?: number
    loadedPlaylistMediaItemId?: number
}

const initialState: PlayMusicPayload = ({})

const playMusicSlice = createSlice({
    name: 'media/music/play',
    initialState,
    reducers: {
        loadTrack(state, action: PayloadAction<LoadPlayMusicPayload>) {
            state.triggerPlay = timestamp()
            state.loadPlaylistMediaItemId = action.payload.loadPlaylistMediaItemId
            state.loadPlaylistId = action.payload.loadPlaylistId
            state.loadedPlaylistMediaItemId = undefined
        },
        loadedTrack(state, action: PayloadAction<number>) {
            state.triggerPlay = undefined
            state.loadPlaylistMediaItemId = undefined
            state.loadPlaylistId = undefined
            state.loadedPlaylistMediaItemId = action.payload
        }
    }
})

export const { loadTrack, loadedTrack} = playMusicSlice.actions


export default playMusicSlice