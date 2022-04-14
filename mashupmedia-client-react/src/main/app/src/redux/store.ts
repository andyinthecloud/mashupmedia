import { configureStore } from '@reduxjs/toolkit';
import { PayloadAction } from './actions';
import rootReducer from './rootReducer';



const initialState = {
    networkProxy: {
        payload: null,
        loading: false,
        error: null
    },
    user: {
        payload: null,
        loading: false,
        error: null
    }
}


// const rootReducer = combineReducers({
//     counter: counterSlice.reducer,
//     loggedInUser: loggedInUserSlice.reducer,
//     user: userSlice.reducer,
//     networkProxy: networkProxySlice.reducer
// })

export const store = configureStore({
    reducer: rootReducer,
    preloadedState: initialState
})



// Infer the `RootState` and `AppDispatch` types from the store itself
// export type RootState = ReturnType<typeof store.getState>
export type RootState = ReturnType<typeof rootReducer>;

// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch




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



