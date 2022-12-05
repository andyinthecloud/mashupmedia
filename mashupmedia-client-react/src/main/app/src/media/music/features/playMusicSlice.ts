import { createSlice } from "@reduxjs/toolkit";

export type PlayMusicPayload = {
    trigger?: number
}

const initialState: PlayMusicPayload = ({})

const playMusicSlice = createSlice({
    name: 'media/music/play',
    initialState,
    reducers: {
        playMusic(state) {
            state.trigger = Date.now()
        }
    }
})

export const { playMusic } = playMusicSlice.actions

export default playMusicSlice