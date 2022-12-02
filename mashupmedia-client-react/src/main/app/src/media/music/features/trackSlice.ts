import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { PayloadState } from "../../../common/redux/store";
import { securityToken } from "../../../common/security/securityUtils";
import { HttpMethod, restHeaders } from "../../../common/utils/httpUtils";

export enum PlaylistAction {
    CURRENT = 'CURRENT',
    NEXT = 'NEXT',
    PREVIOUS = 'PREVIOUS'
}

export type TrackPayload = {
    id: number
    title: string
    artist: string
}

const initialState: PayloadState<TrackPayload> = {
    payload: null,
    error: null,
    loading: false,
}

export const playTrack = createAsyncThunk(
    'security/user',
    async (playlistAction: PlaylistAction,  userToken: string | undefined) => {
        const url: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/media/playlist/audio';
        const response = await fetch(url, {
            method: HttpMethod.PUT,
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(securityToken(userToken)),
            body: JSON.stringify(playlistAction)
        });
        return (await response.json()) as TrackPayload;
    }
)

const trackSlice = createSlice({
    name: 'media/track',
    initialState,
    reducers: {
    },
    extraReducers: (builder) => {
        builder.addCase(loadUserPolicyIntoState.pending, (state) => {
            state.loading = true
            state.error = null
            state.payload = null
        })
        builder.addCase(loadUserPolicyIntoState.rejected, (state, action) => {
            state.loading = false
            state.error = action?.payload ? String(action?.payload) : 'Failed to fetch payload'
            state.payload = null
        })
        builder.addCase(loadUserPolicyIntoState.fulfilled, (state, action) => {
            state.loading = false
            state.error = null
            state.payload = action.payload
        })
    }
})


export default trackSlice