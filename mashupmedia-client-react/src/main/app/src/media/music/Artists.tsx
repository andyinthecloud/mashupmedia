import { Clear } from "@mui/icons-material"
import { IconButton, List, ListItem, ListItemButton, ListItemText, TextField } from '@mui/material'
import { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../common/redux/store'
import { ArtistPayload, getArtists } from './rest/musicCalls'

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



    return (
        <div>
            <h1>Artists</h1>

            <TextField
                label="Search artist"
                variant="outlined"
                sx={{ marginBottom: 1 }}
                fullWidth
                onChange={(e) => handleArtistSearchChange(e.currentTarget.value)}
                InputProps={{
                    endAdornment: 
                    <IconButton onClick={() => handleArtistSearchChange("")}><Clear /></IconButton>
                }}

            />

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