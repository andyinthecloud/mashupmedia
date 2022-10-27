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
    PUT = 'PUT',
    DELETE = 'DELETE'
}

export enum HttpStatus {
    FORBIDDEN = 403,
    SERVER_ERROR = 500,
    NOT_FOUND = 404,
    OK = 200
}

export interface HttpResponse<T> extends Response {
    parsedBody?: T,
    parsedBlob?: Blob
}

export const backendUrl = (uri: string): string => (
    (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + uri
)

export const callMashupMediaApi = async <T>(httpMethod: HttpMethod, uri: string, userToken?: string, body?: string): Promise<HttpResponse<T>> => {

    const url = backendUrl(uri)

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

    if (response.status == HttpStatus.FORBIDDEN || response.status == HttpStatus.SERVER_ERROR) {        
        redirectLogin(response.status)        
    }

    return response
}

export const callMashupMediaResource = async (uri: string, userToken?: string): Promise<Response> => {

    const url = backendUrl(uri)

    const response: Response = await fetch(url, {
        method: HttpMethod.GET,
        mode: 'cors',
        credentials: 'omit',
        headers: restHeaders(securityToken(userToken))
    })

    // try {
    //     response.parsedBody = await response.json()
    // } catch (exception) {
    //     console.log('Error getting json', exception)
    // }

    if (response.status == HttpStatus.FORBIDDEN || response.status == HttpStatus.SERVER_ERROR) {        
        redirectLogin(response.status)        
    }

    return response
}




// const contextUrl: string = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2)) 

export const redirectInternal = (internalUri: string): void => {    
    window.location.href = internalUri
}

export const codeParamName = 'code'
export const jumpUriParamName = 'jump'

export const redirectLogin = (statusCode?: number): void => {    

    console.log('redirectLogin')

    const loginUri = '/login';
    const searchParams = new URLSearchParams()

    if (statusCode) {
        searchParams.append(codeParamName, '' + statusCode)
    }

    searchParams.append(jumpUriParamName, window.location.pathname + window.location.search)

    redirectInternal(loginUri + '?' + searchParams.toString())
}


