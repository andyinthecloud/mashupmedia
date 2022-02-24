import { configureStore } from '@reduxjs/toolkit';
import { counterSlice } from "../security/features/counterSlice";
import loggedInUserSlice from '../security/features/loggedInUserSlice';
import networkProxySlice from '../settings/features/networkSlice';

// const reducer = combineReducers({
//     counter: counterSlice.reducer,
//     loggedInUser: loggedInUserSlice.reducer,
//     networkProxy: networkProxySlice.reducer
// })


export const store = configureStore({
    reducer: {
        counter: counterSlice.reducer,
        loggedInUser: loggedInUserSlice.reducer,
        networkProxy: networkProxySlice.reducer
    }
})



// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch


export enum PayloadAction {
    SAVED,
    GOT
}

export type PayloadState<T> = {
    payload: T | null;
    loading: boolean;
    error: string | null | void;
    payloadAction?: PayloadAction
}

export type SecurePayload<T> = {
    payload: T;
    userToken: string | undefined;
}



