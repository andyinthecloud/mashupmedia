import { combineReducers } from "@reduxjs/toolkit";
import playMusicSlice from "../../media/music/features/playMusicSlice";
import notificationSlice from "../notification/notificationSlice";
import securitySlice from "../security/features/securitySlice";
import userPolicySlice from "../security/features/userPolicySlice";
import menuSlice from "../components/features/menuSlice";
import mediaSearchResultsSlice from "../../media/search/features/searchMediaSlice";

const rootReducer = combineReducers({
    security: securitySlice.reducer,
    userPolicy: userPolicySlice.reducer,
    notification: notificationSlice.reducer,
    playMusic: playMusicSlice.reducer,
    menuState: menuSlice.reducer,
    searchMedia: mediaSearchResultsSlice.reducer
})

export default rootReducer