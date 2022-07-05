import { combineReducers } from "@reduxjs/toolkit";
import { counterSlice } from "../security/features/counterSlice";
import loggedInUserSlice  from "../security/features/loggedInUserSlice";
import networkProxySlice  from "../settings/features/networkSlice";

const rootReducer = combineReducers({
    counter: counterSlice.reducer,
    loggedInUser: loggedInUserSlice.reducer,
    networkProxy: networkProxySlice.reducer
})

export default rootReducer