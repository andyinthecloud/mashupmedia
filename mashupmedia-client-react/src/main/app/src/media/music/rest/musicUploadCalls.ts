import { securityToken } from "../../../common/security/securityUtils"
import { HttpMethod, HttpResponse, backEndUrl, callMashupMediaApi, multiPartHeaders, restHeaders } from "../../../common/utils/httpUtils"


export type MetaImagePayload = {
    ranks: number[]
    message: string
}


const musicUri = "/upload/music"


// export const albumArtImageUrl = (albumId: number, imageType: ImageType, mediaToken: string): string => {
//     // return `${backEndUrl(musicUri)}/${albumId}?mediaToken=${mediaToken}&imageType=${imageType}`
//      callMashupMediaApi<ArtistPayload>(HttpMethod.POST, musicUri, userToken, JSON.stringify(artistPayload))
// }



const postFiles =  async <MetaImagePayload>(uri: string, formData: FormData, userToken?: string): Promise<HttpResponse<MetaImagePayload>> => {

    const url = backEndUrl(uri)    

    const response: HttpResponse<MetaImagePayload> = await fetch(url, {
        method: HttpMethod.POST,
        mode: 'cors',
        credentials: 'omit',
        headers: multiPartHeaders(securityToken(userToken)),
        body: formData
    })

    try {
        response.parsedBody = await response.json()
    } catch (exception) {
        console.log('Error parsing json', response)
    }

    return response
}  

const addFiles = (formData: FormData, fileList: FileList): void => {
    for (let i = 0; i < fileList.length; i++) {
        formData.append("files", fileList[i])
    }
}

const artistUri = musicUri + "/artist"

export const uploadArtistImages = (artistId: number,  fileList: FileList, userToken?: string): Promise<HttpResponse<MetaImagePayload>> => {
    const formData = new FormData()
    formData.append("artistId", "" + artistId)
    addFiles(formData, fileList)
    return postFiles(artistUri + "/images", formData, userToken)
}