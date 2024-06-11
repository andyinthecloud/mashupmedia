import { MetaPayload } from "../../../common/components/meta/metaUtils"
import { securityToken } from "../../../common/security/securityUtils"
import { HttpMethod, HttpResponse, backEndUrl, multiPartHeaders } from "../../../common/utils/httpUtils"

export type MetaImagePayload = & MetaPayload 


const musicUri = "/upload/music"


const postFiles =  async <MetaImagePayload>(uri: string, formData: FormData, userToken?: string): Promise<HttpResponse<MetaImagePayload[]>> => {

    const url = backEndUrl(uri)    

    const response: HttpResponse<MetaImagePayload[]> = await fetch(url, {
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

export const uploadArtistImages = (artistId: number,  fileList: FileList, userToken?: string): Promise<HttpResponse<MetaImagePayload[]>> => {
    const formData = new FormData()
    formData.append("artistId", "" + artistId)
    addFiles(formData, fileList)
    return postFiles(artistUri + "/images", formData, userToken)
}