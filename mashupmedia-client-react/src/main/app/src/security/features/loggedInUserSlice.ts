import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";

type UserCredentialsPayload = {
    username: string
    password: string
}

export type UserPayload = {
    username: string
    name: string
    token: string
    groupNames: string[]
}

export type LogInState = {
    currentUser: UserPayload | null;
    loading: boolean;
    error: string | null;
}

const initialState: LogInState = {
    currentUser: null,
    loading: false,
    error: null,
}

export const logIn = createAsyncThunk<UserPayload, UserCredentialsPayload>(
    'user/login',
    async (userCredentialsPayload: UserCredentialsPayload) => {
        console.log('userCredentialsPayload', userCredentialsPayload);


        const loginUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/login';
        const response = await fetch(loginUrl, {
            method: 'POST',
            mode: 'cors',
            credentials: 'omit',
            headers: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS'
            },
            body: JSON.stringify(userCredentialsPayload)
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
        builder.addCase(logIn.pending, (state, action) => {
            state.loading = true;
            state.currentUser = null;
            state.error = null;
        })
        builder.addCase(logIn.rejected, (state, action) => {
            state.loading = false;
            state.currentUser = null;
            if (action.payload) {
                state.error = action.payload as string;
            } else {
                state.error = 'Failed to log in user';
            }
        })
        builder.addCase(logIn.fulfilled, (state, action) => {
            state.loading = false;
            state.currentUser = action.payload;
            state.error = null;
        })
    }
})

export default loggedInUserSlice
