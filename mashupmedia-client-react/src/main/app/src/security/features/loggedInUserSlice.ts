import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { PayloadState } from "../../redux/store";
import { restHeaders } from "../../utils/httpUtils";

export type UserLogInPayload = {
    username: string
    password: string
}

export type UserPayload = {
    username: string
    name: string
    token: string
    groupNames: string[]
}

const initialState: PayloadState<UserPayload> = {
    payload: null,
    error: null,
    loading: false,
}

export const logIn = createAsyncThunk<UserPayload, UserLogInPayload>(
    'user/login',
    async (userLoginPayload: UserLogInPayload) => {
        const loginUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/login';
        const response = await fetch(loginUrl, {
            method: 'POST',
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders,
            body: JSON.stringify(userLoginPayload)
        });
        return (await response.json()) as UserPayload;
    }
)

export const loggedInUserSlice = createSlice({
    name: 'loggedInUser',
    initialState,
    reducers: {
    },
    extraReducers: (builder) => {
        builder.addCase(logIn.pending, (state) => {
            state.loading = true
            state.error = null
            state.payload = null
        })
        builder.addCase(logIn.rejected, (state, action) => {
            state.loading = false
            state.error = action?.payload ? String(action?.payload) : 'Failed to fetch payload'
            state.payload = null
        })
        builder.addCase(logIn.fulfilled, (state, action) => {
            state.loading = false
            state.error = null
            state.payload = action.payload
        })
    }
})

export default loggedInUserSlice
