
export const restHeaders = (userToken?: string): Headers => {
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
    POST = 'POST'
}

export interface HttpResponse<T> extends Response {
    parsedBody?: T;
}


export const fetchMashupMediaApi = async <T>(httpMethod: HttpMethod, uri: string, userToken?: string, body?: string): Promise<HttpResponse<T>> => {

    const url: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + uri

    const response: HttpResponse<T> = await fetch(url, {
        method: httpMethod.toString(),
        mode: 'cors',
        credentials: 'omit',
        headers: restHeaders(userToken),
        body: body
    })

    try {
        response.parsedBody = await response.json()
    } catch (exception) {
        console.log('Error getting json', exception)
    }


    if (!response.ok) {
        throw new Error(response.statusText)
    }

    return response
}


