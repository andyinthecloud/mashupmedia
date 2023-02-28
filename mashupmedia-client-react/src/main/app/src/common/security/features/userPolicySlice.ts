import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import type { PayloadState } from "../../redux/store";
import { backEndUrl, HttpMethod, restHeaders } from "../../utils/httpUtils";
import { securityToken } from "../securityUtils";

export type UserPolicyPayload = {
    administrator: boolean
    username: string
    name: string
    streamingToken: string
}


const initialState: PayloadState<UserPolicyPayload> = {
    payload: null,
    error: null,
    loading: false
}

export const userPolicy = createAsyncThunk(
    'security/user',
    async (userToken: string | undefined) => {
        const url: string = backEndUrl('/api/security/user-policy')
        const response = await fetch(url, {
            method: HttpMethod.GET,
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(securityToken(userToken))
        });
        return (await response.json()) as UserPolicyPayload;
    }
)

const userPolicySlice = createSlice({
    name: 'loggedInUser',
    initialState,
    reducers: {
        removeUserPolicy: (state) => {
            state.payload = null
        }
    },
    extraReducers: (builder) => {
        builder.addCase(userPolicy.pending, (state) => {
            state.loading = true
            state.error = null
            state.payload = null
        })
        builder.addCase(userPolicy.rejected, (state, action) => {
            state.loading = false
            state.error = action?.payload ? String(action?.payload) : 'Failed to fetch payload'
            state.payload = null
        })
        builder.addCase(userPolicy.fulfilled, (state, action) => {
            state.loading = false
            state.error = null
            state.payload = action.payload
        })
    }
})

export const {removeUserPolicy} = userPolicySlice.actions

export default userPolicySlice
