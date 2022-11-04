import { createAsyncThunk, createSlice } from "@reduxjs/toolkit"
import { PayloadState } from "../../redux/store"
import { HttpMethod, restHeaders } from "../../utils/httpUtils"

export type UserLogInPayload = {
    username: string
    password: string
}

export type UserTokenPayload = {
    token: string
}

const initialState: PayloadState<UserTokenPayload> = {
    payload: null,
    error: null,
    loading: false,
}

export const logIn = createAsyncThunk<UserTokenPayload, UserLogInPayload>(
    'security/login',
    async (userLoginPayload: UserLogInPayload) => {
        const loginUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/security/login';
        const response = await fetch(loginUrl, {
            method: HttpMethod.POST,
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(),
            body: JSON.stringify(userLoginPayload)
        });
        return (await response.json()) as UserTokenPayload;
    }
)


const securitySlice = createSlice({
    name: 'security/login',
    initialState,
    reducers: {},
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

export default securitySlice