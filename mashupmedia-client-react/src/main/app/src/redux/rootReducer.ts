import { combineReducers } from "@reduxjs/toolkit";
import { counterSlice } from "../security/features/counterSlice";
import loggedInUserSlice  from "../security/features/loggedInUserSlice";
import networkProxySlice  from "../settings/features/networkSlice";
import userSlice from "../settings/features/userSlice";

const rootReducer = combineReducers({
    counter: counterSlice.reducer,
    loggedInUser: loggedInUserSlice.reducer,
    user: userSlice.reducer,
    networkProxy: networkProxySlice.reducer
})

export default rootReducer