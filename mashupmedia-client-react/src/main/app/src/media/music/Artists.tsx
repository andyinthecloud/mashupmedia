import { Add, Clear } from "@mui/icons-material"
import { Button, IconButton, List, ListItem, ListItemButton, ListItemText, TextField } from '@mui/material'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { RootState } from '../../common/redux/store'
import './Artists.css'
import { ArtistPayload, createArtist, getArtists } from './rest/musicCalls'

type ArtistsPagePayload = {
    searchName?: string
    artistPayloads: ArtistPayload[]
    filteredArtistPayloads?: ArtistPayload[]
}

const Artists = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const navigate = useNavigate()
    const dispatch = useDispatch()

    const handleCreateArtist = (): void => {

        const name = props.searchName
        if (!name) {
            return
        }

        createArtist({ name }, userToken).then(response => {

            let artistId = 0;
            if (response.ok) {
                artistId = response.parsedBody?.id || 0
                if (artistId) {
                    navigate('/music/artist/' + artistId)
                }
            }

            if (!response.ok || !artistId) {
                dispatch(
                    addNotification({
                        message: 'Unable to create artist.',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })
    }

    const [props, setProps] = useState<ArtistsPagePayload>({
        artistPayloads: []
    });

    useEffect(() => {
        getArtists(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(p => ({
                    ...p,
                    artistPayloads: response.parsedBody || []
                }))
            }
        })

    }, [userToken])

    const handleArtistSearchChange = (artistSearchValue: string): void => {
        setProps(p => ({
            ...p,
            searchName: artistSearchValue
        }))
    }


    useEffect(() => {
        if (!props.searchName) {
            return
        }

        setProps(p => ({
            ...p,
            filteredArtistPayloads: p.artistPayloads.filter(artistPayload =>
                artistPayload.name.toLowerCase().startsWith(p.searchName || '')
            )
        }))

    }, [props.searchName])


    const getArtistPayloads = (): ArtistPayload[] =>
        props.searchName ? (props.filteredArtistPayloads || []) : props.artistPayloads


    const showNewArtist = (): boolean => {
        
        const searchNameLowerCase = props.searchName?.toLowerCase()
        
        if (!searchNameLowerCase) {
            return false
        }

        if (!props.filteredArtistPayloads?.length) {
            return true
        }

        return props.filteredArtistPayloads.some(
            artistPayload => 
                artistPayload.name.toLowerCase() !== searchNameLowerCase
        )
    }

    return (
        <div id="artists">

            <div className="title">
                <h1>Artists</h1>
            </div>

            <TextField
                label="Search / new artist"
                variant="outlined"
                fullWidth
                onChange={(e) => handleArtistSearchChange(e.currentTarget.value)}
                value={props.searchName || ''}
                InputProps={{
                    endAdornment:
                        <IconButton onClick={() => handleArtistSearchChange("")}><Clear /></IconButton>
                }}
            />

            {showNewArtist() &&
                <div className="new-line right" style={{marginTop: "1em"}}>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={
                            <Add />
                        }
                        onClick={handleCreateArtist}
                    >New</Button>
                </div>
            }

            <List>
                {getArtistPayloads().map(function (artist) {
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