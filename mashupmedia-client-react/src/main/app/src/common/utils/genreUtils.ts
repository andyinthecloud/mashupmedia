import { t } from "i18next"
import { GenrePayload } from "../../configuration/backend/metaCalls"

export const GENRE_AUTOMATIC = "AUTOMATIC"

export const prepareUploadGenrePayloads = (genrePayloads: GenrePayload[]): GenrePayload[] => {
    genrePayloads.unshift({idName: GENRE_AUTOMATIC, name: t("uploadArtistTracks.metaTag")})
    return genrePayloads
}

export const genreNames = (genres: GenrePayload[], idNames: string[]): string[] => {
    const names: string[] = []
    
    idNames.map(idName => {
        names.push(genreName(genres, idName))
    })

    return names
}

const genreName = (genres: GenrePayload[], idName: string): string => {
    const genre = genres.find(genre => genre.idName === idName)
    return genre ? genre.name : ''
}