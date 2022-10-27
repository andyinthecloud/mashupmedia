import { List, ListItem, ListItemButton, ListItemText } from '@mui/material'
import { useEffect, useState } from 'react'
import { useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { RootState } from '../../common/redux/store'
import { ArtistPayload, getArtists } from './rest/musicCalls'

const Artists = () => {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<ArtistPayload[]>([])

    useEffect(() => {
        getArtists(userToken).then(response => {
            if (response.parsedBody !== undefined) {
                setProps(response.parsedBody)
            }
        })

    }, [userToken])



    return (
        <div>
            <h1>Artists</h1>
            <List>
                {props.map(function (artist) {
                    return (
                        <ListItem
                            key={artist.id}
                            component={Link}
                            to={"/music/artist/" + artist.id}
                            disablePadding>
                            <ListItemButton>
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