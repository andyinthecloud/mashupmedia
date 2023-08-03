import { ExpandMore } from "@mui/icons-material";
import { Accordion, AccordionDetails, AccordionSummary, Button, FormControl, InputLabel, MenuItem, OutlinedInput, Select, SelectChangeEvent, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "../../common/redux/store";
import { prepareGenrePayloads } from "../../common/utils/genreUtils";
import { GenrePayload, MediaItemSearchCriteriaPayload, getGenres } from "./rest/searchCalls";

type SearchFormPayload = {
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    genrePayloads: GenrePayload[]
    decades: number[]
}

export type ExternalSearchFormPayload = {
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    handleSearchMedia: (mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload) => void
}

const SearchForm = (externalSearchFormPayload: ExternalSearchFormPayload) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<SearchFormPayload>({
        genrePayloads: [],
        decades: []
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: externalSearchFormPayload.mediaItemSearchCriteriaPayload
        }))
    }, [externalSearchFormPayload])

    useEffect(() => {
        const decades: number[] = []
        for (let decade = 1920; decade < new Date().getFullYear(); decade += 10) {
            decades.push(decade)
        }

        setProps(p => ({
            ...p,
            decades
        })
        )

        getGenres(userToken)
            .then(response => {
                const genrePayloads = prepareGenrePayloads(response.parsedBody || [])

                setProps(p => ({
                    ...p,
                    genrePayloads
                }))
            })

    }, [userToken])

    const handleSearchTextChange = (searchText: string) => {
        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                ...p.mediaItemSearchCriteriaPayload,
                searchText
            }
        }))
    }

    const handleGenreChange = (event: SelectChangeEvent<string[]>) => {

        const genreIdNames: string[] = Array.isArray(event.target.value) ? event.target.value : []
        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                ...p.mediaItemSearchCriteriaPayload,
                genreIdNames
            }
        }))
    }

    const handleDecadeChange = (event: SelectChangeEvent<number[]>) => {
        const decades: number[] = Array.isArray(event.target.value) ? event.target.value : []
        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                ...p.mediaItemSearchCriteriaPayload,
                decades
            }
        }))
    }

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()
        externalSearchFormPayload.handleSearchMedia(props.mediaItemSearchCriteriaPayload)

    }

    return (

        <Accordion>
            <AccordionSummary
                expandIcon={<ExpandMore fontSize="medium" color="secondary" />}
                aria-controls="panel1a-content"
                id="panel1a-header"
            >
                <h2>Filter</h2>
            </AccordionSummary>
            <AccordionDetails>
                <form onSubmit={handleSubmit}>

                    <div className="new-line">
                        <TextField
                            name="text"
                            label="Search text"
                            value={props.mediaItemSearchCriteriaPayload?.searchText || ""}
                            onChange={e => handleSearchTextChange(e.currentTarget.value)}
                            fullWidth={true}
                        />
                    </div>

                    <div className="new-line">
                        <FormControl sx={{ width: 1 }}>
                            <InputLabel id="search-form-genres">Genre</InputLabel>
                            <Select
                                labelId="search-form-genres"
                                multiple
                                value={props.mediaItemSearchCriteriaPayload?.genreIdNames || []}
                                input={<OutlinedInput label="Genre" />}
                                onChange={handleGenreChange}
                            >
                                {props.genrePayloads.map((genrePayload) => (
                                    <MenuItem
                                        key={genrePayload.idName}
                                        value={genrePayload.idName}
                                    >
                                        {genrePayload.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </div>

                    <div className="new-line">
                        <FormControl sx={{ width: 1 }}>
                            <InputLabel id="search-form-decades">Decade</InputLabel>
                            <Select
                                labelId="search-form-decades"
                                multiple
                                value={props.mediaItemSearchCriteriaPayload?.decades || []}
                                input={<OutlinedInput label="Decade" />}
                                onChange={handleDecadeChange}
                            >
                                {props.decades.map((decade) => (
                                    <MenuItem
                                        key={decade}
                                        value={decade}
                                    >
                                        {decade}
                                    </MenuItem>
                                ))}

                            </Select>

                        </FormControl>
                    </div>

                    <div className="new-line right">
                        <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                            Cancel
                        </Button>

                        <Button variant="contained" color="primary" type="submit">
                            Search
                        </Button>
                    </div>
                </form>
            </AccordionDetails>
        </Accordion>
    )
}

export default SearchForm


