import { configureStore } from '@reduxjs/toolkit';
import { counterSlice } from "../security/features/counterSlice";
import loggedInUserSlice from '../security/features/loggedInUserSlice';
import networkProxySlice from '../settings/features/networkSlice';

// ...

export const store = configureStore({
    reducer: {
        // posts: postsReducer,
        // comments: commentsReducer,
        // users: usersReducer,
        counter: counterSlice.reducer,
        loggedInUser: loggedInUserSlice.reducer,
        networkProxy: networkProxySlice.reducer

    },
})


// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch


export type PayloadState<T> = {
    payload: T | null;
    loading: boolean;
    error: string | null | void;
}




