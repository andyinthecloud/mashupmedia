import { combineReducers } from "@reduxjs/toolkit";
import notificationSlice from "../notification/notificationSlice";
import securitySlice from "../security/features/securitySlice";
import userPolicySlice from "../security/features/userPolicySlice";
import networkProxySlice from "../settings/features/networkSlice";

const rootReducer = combineReducers({
    security: securitySlice.reducer,
    userPolicy: userPolicySlice.reducer,
    networkProxy: networkProxySlice.reducer,
    notification: notificationSlice.reducer
})

export default rootReducer