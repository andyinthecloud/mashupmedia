import { ExpandMore } from "@mui/icons-material";
import { Accordion, AccordionDetails, AccordionSummary, Button, FormControl, InputLabel, MenuItem, OutlinedInput, Select, SelectChangeEvent, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "../../common/redux/hooks";
import { RootState } from "../../common/redux/store";
import { prepareGenrePayloads } from "../../common/utils/genreUtils";
import { NameValuePayload } from "../../configuration/backend/metaCalls";
import { MashupMediaType } from "../music/rest/playlistActionCalls";
import { MediaItemSearchCriteriaPayload, SortType, searchMedia } from "./features/searchMediaSlice";
import { GenrePayload, getGenres, getOrderByNames } from "./rest/searchCalls";
import { objectToQueryParameters } from "../../common/utils/httpUtils";

type SearchFormPayload = {
    mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload
    genrePayloads: GenrePayload[]
    decades: number[]
    orderBys: NameValuePayload<string>[]
    isAccordionExpanded: boolean
}

const SearchForm = (mediaItemSearchCriteriaPayload?: MediaItemSearchCriteriaPayload) => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<SearchFormPayload>({
        genrePayloads: [],
        decades: [],
        orderBys: [],
        mediaItemSearchCriteriaPayload: {
            mashupMediaType: MashupMediaType.MUSIC
        },
        isAccordionExpanded: true
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                searchText: mediaItemSearchCriteriaPayload?.searchText
            },
            isAccordionExpanded: true
        }))
    }, [mediaItemSearchCriteriaPayload?.searchText])

    useEffect(() => {
        const decades: number[] = []
        for (let decade = 1920; decade < new Date().getFullYear(); decade += 10) {
            decades.push(decade)
        }

        setProps(p => ({
            ...p,
            decades
        }
        ))

        getGenres(userToken)
            .then(response => {
                const genrePayloads = prepareGenrePayloads(response.parsedBody || [])
                setProps(p => ({
                    ...p,
                    genrePayloads
                }))
            })

        getOrderByNames(userToken)
            .then(response => {
                const orderBys = response.parsedBody || []
                setProps(p => ({
                    ...p,
                    orderBys
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

    const handleOrderByChange = (value: string): void => {
        console.log('handleOrderByChange', value)

        const orderBy = props.orderBys.find(orderBy => orderBy.value === value)

        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                ...p.mediaItemSearchCriteriaPayload,
                orderBy: orderBy?.value
            }
        }))


    }

    const handleSortByChange = (sortBy: SortType): void => {
        console.log('handleSortByChange', sortBy)


        setProps(p => ({
            ...p,
            mediaItemSearchCriteriaPayload: {
                ...p.mediaItemSearchCriteriaPayload,
                sortBy
            }
        }))
    }

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/')
    }


    const dispatch = useAppDispatch()

    function handleSubmit(e: React.FormEvent<HTMLFormElement>): void {
        e.preventDefault()

        if (!props.mediaItemSearchCriteriaPayload) {
            return
        }

        navigate(`/search/media/${objectToQueryParameters(props.mediaItemSearchCriteriaPayload)}`)

        dispatch(
            searchMedia({
                token: userToken || '',
                mediaItemSearchCriteriaPayload: props.mediaItemSearchCriteriaPayload
            })
        )
    }

    const toggleAccordionExpand = () => {
        console.log('toggleAccordionExpand')
        setProps(p => ({
            ...p,
            isAccordionExpanded: !p.isAccordionExpanded
        }))
    }

    return (

        <Accordion
            expanded={props.isAccordionExpanded}
            onChange={toggleAccordionExpand}
        >
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

                    <div className="new-line">
                        <FormControl sx={{ minWidth: 0.3 }}>
                            <InputLabel id="order-property-label">Order by</InputLabel>
                            <Select
                                labelId="order-property-label"
                                label="Order by"
                                value={props.mediaItemSearchCriteriaPayload?.orderBy || ''}
                                onChange={e => handleOrderByChange(e.target.value)}
                            >
                                {props.orderBys.map(orderBy => (
                                    <MenuItem key={orderBy.value} value={orderBy.value}>{orderBy.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl sx={{ marginLeft: 5, minWidth: 0.3 }}>
                            <InputLabel id="sort-property-label">Sort by</InputLabel>
                            <Select
                                labelId="sort-property-label"
                                label="Sort by"
                                value={props.mediaItemSearchCriteriaPayload?.sortBy || ""}
                                onChange={e => handleSortByChange(e.target.value as SortType)}
                            >
                                <MenuItem value={SortType.ASC}>Ascending</MenuItem>
                                <MenuItem value={SortType.DESC}>Descending</MenuItem>
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


