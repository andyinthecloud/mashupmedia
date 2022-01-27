import { PayloadState } from "../redux/store";
import { UserPayload } from "./features/loggedInUserSlice";

const TOKEN_KEY = 'jwt';

export const login = () => {
    localStorage.setItem(TOKEN_KEY, 'TestLogin');
}

export const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
}

export const isLogin = () => {

    if (localStorage.getItem(TOKEN_KEY)) {
        return true;
    }

    return false;
}


export const isLoggedIn = (payloadState: PayloadState<UserPayload | null>) => {
    console.log('isLoggedIn', payloadState)
    const token = payloadState.payload?.token;
    console.log('token', token ? true : false)
    return token ? true : false;

    // return true
}


