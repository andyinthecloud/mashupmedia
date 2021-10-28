import {createSlice} from "@reduxjs/toolkit";


interface LoggedInUserState {
    username: string
    name: string
    token: string
    groupNames: string[]
}

const initialState: LoggedInUserState = {
    username: '',
    name: '',
    token: '',
    groupNames: []
}

export const loggedInUserSlice = createSlice({
    name: 'loggedInUser',
    initialState,
    reducers: {}
})