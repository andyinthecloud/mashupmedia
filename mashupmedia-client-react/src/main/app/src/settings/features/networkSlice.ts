import { createAsyncThunk, createSlice } from "@reduxjs/toolkit"
import { PayloadState } from "../../redux/store"
import { restHeaders } from "../../utils/httpUtils"

export type NetworkProxyPayload = {
    enabled: boolean
    url: string
    port: number
    username: string
    password: string
}

const initialState: PayloadState<NetworkProxyPayload> = {
    payload: null,
    loading: false,
    error: null

}

export const getNetworkProxy = createAsyncThunk(
    'settings/network',
    async () => {
        const networkProxyUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/admin/proxy';
        const response = await fetch(networkProxyUrl, {
            method: 'GET',
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders
        })

        return (await response.json()) as NetworkProxyPayload
    }
)

export const networkProxySlice = createSlice({
    name: 'networkProxy',
    initialState,
    reducers: {

    },
    extraReducers: (builder) => {
        builder.addCase(getNetworkProxy.pending, (state) => {
            state = {
                loading: true,
                payload: null,
                error: null
            }
        })
        builder.addCase(getNetworkProxy.rejected, (state, action) => {
            state = {
                loading: false,
                payload: null,
                error: action?.payload ? String(action?.payload) : 'Failed to fetch payload'
            }
        })
        builder.addCase(getNetworkProxy.fulfilled, (state, action) => {
            state = {
                loading: false,
                error: null,
                payload: action.payload
            }
        })

    }
})

export default networkProxySlice