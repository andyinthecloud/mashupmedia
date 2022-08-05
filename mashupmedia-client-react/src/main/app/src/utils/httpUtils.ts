import { securityToken } from "../security/securityUtils"

export const restHeaders = (userToken?: string | null): Headers => {
    const headers = new Headers()
    if (userToken) {
        headers.set('Authorization', 'Bearer ' + userToken)
    }
    headers.set('Content-Type', 'application/json')
    headers.set('Access-Control-Allow-Origin', '*')
    headers.set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')

    return headers
}

export enum HttpMethod {
    GET = 'GET',
    POST = 'POST',
    PUT = 'PUT'
}

export enum HttpStatus {
    FORBIDDEN = 403,
    SERVER_ERROR = 500,
    NOT_FOUND = 404,
    OK = 200
}

export interface HttpResponse<T> extends Response {
    parsedBody?: T;
}

export const callMashupMediaApi = async <T>(httpMethod: HttpMethod, uri: string, userToken?: string, body?: string): Promise<HttpResponse<T>> => {

    const url: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + uri

    const response: HttpResponse<T> = await fetch(url, {
        method: httpMethod.toString(),
        mode: 'cors',
        credentials: 'omit',
        headers: restHeaders(securityToken(userToken)),
        body: body
    })

    try {
        response.parsedBody = await response.json()
    } catch (exception) {
        console.log('Error getting json', exception)
    }

    if (response.status == HttpStatus.FORBIDDEN) {        
        redirectLogin(HttpStatus.FORBIDDEN)        
    }

    if (!response.ok) {
        redirectLogin(HttpStatus.SERVER_ERROR);
    }

    return response
}


// const contextUrl: string = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2)) 

export const redirectInternal = (internalUri: string): void => {    
    // console.log('redirectInternal', contextUrl + internalUri)
    window.location.href = internalUri

}

export const codeParamName = 'code'

export const redirectLogin = (httpStatus: HttpStatus): void => {
    
    let loginUri = '/login'
    if (httpStatus) {
        loginUri += '?' + codeParamName + '=' + httpStatus
    }

    redirectInternal(loginUri)
}


