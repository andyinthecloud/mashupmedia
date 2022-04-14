import { createAsyncThunk, createSlice } from "@reduxjs/toolkit"
import { PayloadAction } from "../../redux/actions"
import type { PayloadState, SecurePayload } from "../../redux/store"
import { restHeaders } from "../../utils/httpUtils"

export type NetworkProxyPayload = {
    enabled: boolean
    url?: string
    port?: number
    username?: string
    password?: string
}

const initialState: PayloadState<NetworkProxyPayload> = {
    payload: {
        enabled: false,
        url: '',
        port: 0,
        username: '',
        password: ''
    },
    loading: false,
    error: null
}


const networkProxyUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/admin/proxy/'

export const getNetworkProxy = createAsyncThunk(
    'networkProxy/getDetails',
    async (userToken: string | undefined) => {
        const response = await fetch(networkProxyUrl, {
            method: 'GET',
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(userToken)
        })

        return (await response.json()) as NetworkProxyPayload
    }
)

export const postNetworkProxy = createAsyncThunk(
    'networkProxy/postDetails',
    async (securePayload: SecurePayload<NetworkProxyPayload>) => {
        const response = await fetch(networkProxyUrl, {
            method: 'PUT',
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(securePayload.userToken),
            body: JSON.stringify(securePayload.payload)
        })

        return (await response.json()) as NetworkProxyPayload

    }
)

const networkProxySlice = createSlice({
    name: 'networkProxy',
    initialState,
    reducers: {

    },
    extraReducers: (builder) => {

        builder.addCase(
            getNetworkProxy.fulfilled,
            (state, action) => {
                state.loading = true
                state.error = null
                state.payload = action.payload
                state.payloadAction = PayloadAction.RETRIEVED
            })
        builder.addCase(
            postNetworkProxy.fulfilled,
            (state, action) => {
                state.loading = true
                state.error = null
                state.payload = action.payload
                state.payloadAction = PayloadAction.SAVED
            })


        builder.addMatcher(
            (action) => action.type.endsWith('/pending'),
            (state) => {
                state.loading = true
                state.error = null
                state.payload = null
                state.payloadAction = undefined
            }
        )
        builder.addMatcher(
            (action) => action.type.endsWith('/rejected'),
            (state, action) => {
                state.loading = true
                state.error = action?.payload ? String(action?.payload) : 'Failed to fetch payload'
                state.payload = null
                state.payloadAction = undefined
            }
        )
    }
})

// export const selectProxy = (state: RootState) => state.networkProxy.payload

export default networkProxySlice