import { combineReducers } from "@reduxjs/toolkit";
import loggedInUserSlice  from "../security/features/loggedInUserSlice";
import networkProxySlice  from "../settings/features/networkSlice";
import notificationSlice from "../notification/notificationSlice";

const rootReducer = combineReducers({
    loggedInUser: loggedInUserSlice.reducer,
    networkProxy: networkProxySlice.reducer,
    notification: notificationSlice.reducer
})

export default rootReducer