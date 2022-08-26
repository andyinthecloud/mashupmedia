
const TOKEN_KEY = 'jwt'

// export const login = () => {
//     localStorage.setItem(TOKEN_KEY, 'TestLogin');
// }

// export const logout = () => {
//     localStorage.removeItem(TOKEN_KEY);
// }

// export const isLogin = (): boolean => {

//     if (localStorage.getItem(TOKEN_KEY)) {
//         return true;
//     }

//     return false;
// }

// export const isLoggedIn = async (userToken: string | null | undefined): boolean => {
//     // console.log('isLoggedIn', userPayload)
//     // const token = userPayload?.token
//     console.log('token', userToken ? true : false)

//     if (!userToken) {
//         return false
//     }

//     // fetchMyAccount(token);
//     const isLoggedIn = 
//     fetchMyAccount(userToken).then((response) => {
//         return response.ok;
//     }).catch(error => {
//         return false
//     })



//     // return userToken ? true : false
//     return await isLoggedIn;
// }

export function setTokenCookie(token: string | undefined): void {
    if (!token?.length) {
        return;
    }

    const date = new Date()
    date.setMonth(date.getMonth() + 3) 
    document.cookie = `${TOKEN_KEY}=${token}; expires = ${date.toUTCString()}; path = /`
}

export function getCookieValue (name: string): string | null {
	const value = `; ${document.cookie}`;
	const parts = value.split(`; ${name}=`);

    if (parts && parts.length === 2) {
        const value = parts.pop()?.split(';').shift()
        return value === undefined ? null : value
    } else {
        return null
    }
}


export const securityToken = (userToken: string | undefined): string | null => 
    userToken ? userToken : getCookieValue(TOKEN_KEY)

