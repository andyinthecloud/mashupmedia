import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { PayloadAction } from "../../redux/actions";
import type { PayloadState } from "../../redux/store"
import { restHeaders } from "../../utils/httpUtils"
import { GroupPayload, RolePayload } from "../ajax/metaCalls";





export type UserPayload = {
    admin: boolean;
    enabled: boolean;
    editable: boolean;
    username: string;
    name: string;
    createdOn?: string | null;
    updatedOn?: string | null;
    rolePayloads?: RolePayload[];
    groupPayloads?: GroupPayload[];

}

const userUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/admin/user/'

export const getMyAccount = createAsyncThunk(
    'user/myAccount',
    async (userToken: string | undefined) => {
        const response = await fetch(userUrl + 'me', {
            method: 'GET',
            mode: 'cors',
            credentials: 'omit',
            headers: restHeaders(userToken)
        })

        return (await response.json()) as UserPayload
    }
)



const initialState: PayloadState<UserPayload> = {
    payload: {
        admin: false,
        enabled: false,
        editable: false,
        username: '',
        name: '',
        createdOn: null,
        updatedOn: null,
        rolePayloads: [],
        groupPayloads: []
    },
    loading: false,
    error: null
}




 const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {

    },
    extraReducers: (builder) => {
        builder.addCase(
            getMyAccount.fulfilled,
            (state, action) => {
                state.loading = true
                state.error = null
                state.payload = action.payload
                state.payloadAction = PayloadAction.RETRIEVED
            }
        )


        // builder.addMatcher(
        //     (action) => action.type.endsWith('/fulfilled'),
        //     (state, action) => {
        //         state.loading = true
        //         state.error = null
        //         state.payload = action.payload
        //         state.payloadAction = PayloadAction.RETRIEVED
        //     }
        // )

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

        builder.addDefaultCase ((state, action) => {
            state.loading = true
            state.error = null
            state.payload = null
            state.payloadAction = undefined
        })

        
    }
})

export default userSlice