import { Search } from "@mui/icons-material";
import MenuIcon from '@mui/icons-material/Menu';
import { AppBar, Box, IconButton, InputAdornment, TextField, Toolbar } from "@mui/material";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import logoSmall from '../../logo-small.png';
import './MashupBar.css';
import MenuDrawer, { MenuDrawerPayload } from "./MenuDrawer";
import { isEnterKey } from "../utils/formUtils";

type MashupBarPayload = {
    searchText?: string
    menuDrawerPayload: MenuDrawerPayload
}

const MashupBar = () => {

    const [props, setProps] = useState<MashupBarPayload>({
        menuDrawerPayload: ({
            openMenu: false
        })
    })

    const handleChangeSearchText = (searchText: string): void => {
        setProps({
            searchText,
            menuDrawerPayload: {
                openMenu: false
            }
        })
    }

    const navigate = useNavigate()
    const handleClickSearch = (): void => {
        if (!props.searchText) {
            return
        }
        navigate(`/search/media?search=${encodeURIComponent(props.searchText)}`)
    }

    const handleClickToggleOpenMenu = () => {
        setProps(p => ({
            ...p,
            menuDrawerPayload: {
                openMenu: true
            }
        }))
    }

    return (
        <Box sx={{ flexGrow: 1 }} id="mashup-bar">
            <MenuDrawer {...props.menuDrawerPayload} />
            <AppBar
                elevation={0}
                style={{
                    background: "#3a2944"
                }}>
                <Toolbar>
                    <IconButton
                        size="large"
                        edge="start"
                        color="primary"
                        aria-label="menu"
                        sx={{
                            mr: 2
                        }}
                        onClick={handleClickToggleOpenMenu}
                    >
                        <MenuIcon />
                    </IconButton>

                    <Link to={"/"}>
                        <img src={logoSmall} className="logo" />
                    </Link>

                    <TextField
                        className="search"
                        placeholder="Search"
                        variant="outlined"
                        name="searchText"
                        value={props.searchText || ""}
                        onChange={e => handleChangeSearchText(e.target.value)}
                        onKeyDown={e => isEnterKey(e.key) && handleClickSearch()}
                        sx={{
                            "& fieldset": { border: 'none' }
                        }}
                        InputProps={{
                            endAdornment: (

                                <IconButton
                                    onClick={handleClickSearch}
                                >
                                    <Search
                                        className="text-color"
                                        color="primary"
                                    />

                                </IconButton>
                            )
                        }}
                    />
                </Toolbar>
            </AppBar>
        </Box>
    )
}

export default MashupBar