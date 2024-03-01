import { Add, Clear } from "@mui/icons-material"
import { Button, IconButton, List, ListItem, ListItemButton, ListItemText, TextField } from '@mui/material'
import { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { RootState } from '../../common/redux/store'
import { ArtistPayload, getArtists } from './rest/musicCalls'
import './Artists.css'

const Artists = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<ArtistPayload[]>([])

    const [filteredProps, setFilteredProps] = useState<ArtistPayload[]>([])

    const [artistSearch, setArtistSearch] = useState<string>("")

    useEffect(() => {
        getArtists(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
                setFilteredProps(response.parsedBody)

            }
        })

    }, [userToken])

    const handleArtistSearchChange = (artistSearchValue: string): void => {
        setArtistSearch(artistSearchValue.toLowerCase())
    }


    useEffect(() => {
        if (!artistSearch) {
            setFilteredProps(props)
            return
        }

        setFilteredProps(
            props.filter(artistPayload =>
                artistPayload.name.toLowerCase().startsWith(artistSearch || "")
            )
        )

    }, [artistSearch, props])


    const showSearchArtists = (): boolean => (
        props?.length > 20
    )

    const navigate = useNavigate()
    const handleClickNewArtist = () => {
        navigate('/music/artist')
    }

    return (
        <div id="artists">
            <div className="title">
                <h1>Artists</h1>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={
                        <Add />
                    }
                    onClick={handleClickNewArtist}
                >New</Button>
            </div>


            {showSearchArtists() &&
                <TextField
                    label="Search artist"
                    variant="outlined"
                    fullWidth
                    onChange={(e) => handleArtistSearchChange(e.currentTarget.value)}
                    InputProps={{
                        endAdornment:
                            <IconButton onClick={() => handleArtistSearchChange("")}><Clear /></IconButton>
                    }}

                />
            }

            <List>
                {filteredProps.map(function (artist) {
                    return (
                        <ListItem
                            key={artist.id}
                            component={Link}
                            to={"/music/artist/" + artist.id}
                            disablePadding>
                            <ListItemButton
                                sx={{
                                    borderBottom: 1,
                                    borderBottomColor: "darkgrey",
                                    color: "black"
                                }}
                            >
                                <ListItemText
                                    primary={artist.name}
                                />
                            </ListItemButton>
                        </ListItem>
                    )
                })}
            </List>
        </div>
    )
}

export default Artists