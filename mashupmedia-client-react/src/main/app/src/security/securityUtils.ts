import type { UserPayload } from "./features/loggedInUserSlice";

const TOKEN_KEY = 'jwt'
const COOKIE_TOKEN_NAME = 'token'

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

export const isLoggedIn = (userPayload: UserPayload | null) => {
    console.log('isLoggedIn', userPayload)
    const token = userPayload?.token
    console.log('token', token ? true : false)
    return token ? true : false
}

export function setTokenCookie(token: string): void {
    const date = new Date()
    date.setMonth(date.getMonth() + 3) 
    document.cookie = `${COOKIE_TOKEN_NAME}=${token}; expires = ${date.toUTCString()}; path = /`
}

export function getCookieValue (name: string): string | undefined | null  {
	let value = `; ${document.cookie}`;
	let parts = value.split(`; ${name}=`);

    if (parts && parts.length === 2) {
        return parts.pop()?.split(';').shift()
    } else {
        return null
    }
}




