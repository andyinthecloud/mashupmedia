import { Clear, Search } from "@mui/icons-material";
import MenuIcon from '@mui/icons-material/Menu';
import { AppBar, Box, IconButton, TextField, Toolbar } from "@mui/material";
import { useState } from "react";
import { useDispatch } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import icon from '../../icon.png';
import { isEnterKey } from "../utils/formUtils";
import './MashupBar.css';
import MenuDrawer from "./MenuDrawer";
import { openMenu } from "./features/menuSlice";

type MashupBarPayload = {
    searchText?: string
}

const MashupBar = () => {

    const [props, setProps] = useState<MashupBarPayload>()

    const handleChangeSearchText = (searchText: string): void => {
        setProps({
            searchText
        })
    }

    const navigate = useNavigate()

    const handleClickSearch = (): void => {
        if (!props?.searchText) {
            return
        }
        navigate(`/search/media?search=${encodeURIComponent(props.searchText)}`)
    }

    const handleClearSearch = (): void => {
        setProps({

        })
    }


    const dispatch = useDispatch()

    const handleClickToggleOpenMenu = () => {
        console.log("handleClickToggleOpenMenu")

        dispatch(
            openMenu()
        )
    }

    return (
        <Box sx={{ flexGrow: 1 }} id="mashup-bar">
            <MenuDrawer />
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
                        <img src={icon} className="icon" />
                    </Link>

                    <TextField
                        className="search"
                        placeholder="Search"
                        variant="outlined"
                        name="searchText"
                        value={props?.searchText || ""}
                        onChange={e => handleChangeSearchText(e.target.value)}
                        onKeyDown={e => isEnterKey(e.key) && handleClickSearch()}
                        sx={{
                            "& fieldset": { border: 'none' }
                        }}
                        InputProps={{
                            endAdornment: (

                                <IconButton
                                    onClick={handleClearSearch}
                                >
                                    <Clear
                                        className="text-color"
                                        color="primary"
                                    />

                                </IconButton>
                            )
                        }}
                    />
                    <IconButton
                        onClick={handleClickSearch}
                    >
                        <Search
                            className="text-color"
                            color="primary"
                        />
                    </IconButton>
                </Toolbar>
            </AppBar>
        </Box>
    )
}

export default MashupBar