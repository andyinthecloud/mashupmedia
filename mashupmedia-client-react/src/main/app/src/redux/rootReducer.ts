import { combineReducers } from "@reduxjs/toolkit";
import notificationSlice from "../common/notification/notificationSlice";
import securitySlice from "../common/security/features/securitySlice";
import userPolicySlice from "../common/security/features/userPolicySlice";
import networkProxySlice from "../configuration/features/networkSlice";

const rootReducer = combineReducers({
    security: securitySlice.reducer,
    userPolicy: userPolicySlice.reducer,
    networkProxy: networkProxySlice.reducer,
    notification: notificationSlice.reducer
})

export default rootReducer