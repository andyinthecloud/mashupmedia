import { callMashupMediaApi, callMashupMediaResource, HttpMethod, HttpResponse } from '../../common/utils/httpUtils'

const albumArtUri = '/api/music/artists/album-art/'

export enum AlbumArtImageType {
    ORIGINAL = 'original',
    THUMBNAIL = 'thumbnail'
}

export const getAlbumArtImageUrl = (albumId: number, albumArtImageType: AlbumArtImageType): string => {    
    return albumArtUri + albumId + '/' + albumArtImageType
}


export const getImage = (url: string, userToken?: string): Promise<Response> => {
    return callMashupMediaResource ( url, userToken)
}