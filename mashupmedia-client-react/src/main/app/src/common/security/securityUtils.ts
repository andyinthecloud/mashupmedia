
const TOKEN_KEY = 'jwt'

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

