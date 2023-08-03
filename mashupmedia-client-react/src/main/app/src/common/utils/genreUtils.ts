import { GenrePayload } from "../../media/search/rest/searchCalls";


export const prepareGenrePayloads = (genrePayloads: GenrePayload[]): GenrePayload[] => {
    const preparedGenrePayloads: GenrePayload[] = []

    genrePayloads.map(genrePayload => {
        preparedGenrePayloads.push({
            id: genrePayload.id,
            idName: genrePayload.idName,
            name: getGenreName(genrePayload.idName)

        })
    })
    
    return preparedGenrePayloads.sort((g1, g2) => {
        if (g1.name < g2.name) {
            return -1
        }  
        
        if (g1.name > g2.name) {
            return 1
        }  

        return 0
    })
}


const getGenreName = (idName: string): string => {
    switch (idName) {
        case "world_and_traditional_folk":
            return "World and traditional folk";
        case "latin":
            return "Latin";
        case "hip_hop_and_rap":
            return "Hip hop and rap";
        case "r_and_b":
            return "Rhythm and blues";
        case "metal":
            return "Metal";
        case "country":
            return "Country";
        case "folk_and_acoustic":
            return "Folk and acoustic";
        case "classical":
            return "Classical";
        case "JAZZ":
            return "Jazz";
        case "blues":
            return "Blues";
        case "easy_listening":
            return "Easy listening";
        case "new_age":
            return "New age";
        case "rock":
            return "Rock";
        case "pop":
            return "Pop";
        case "dance":
            return "Dance";

        default:
            return "Other"
    }
}