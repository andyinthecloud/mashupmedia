import { Search } from "@mui/icons-material";
import MenuIcon from '@mui/icons-material/Menu';
import { AppBar, Box, IconButton, InputAdornment, TextField, Toolbar } from "@mui/material";
import { useState } from "react";
import { Link } from "react-router-dom";
import logoSmall from '../../logo-small.png';
import './MashupBar.css';
import MenuDrawer, { MenuDrawerPayload } from "./MenuDrawer";

const MashupBar = () => {

    const [props, setProps] = useState<MenuDrawerPayload>({
        openMenu: false
    })

    const handleMenuClick = () => {
        setProps(p => ({
            ...props,
            openMenu: true
        }))
    }

    return (
        <Box sx={{ flexGrow: 1 }} id="mashup-bar">
            <MenuDrawer {...props} />
            <AppBar elevation={0}>
                <Toolbar>
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="menu"
                        sx={{ mr: 2 }}
                        onClick={handleMenuClick}
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
                        sx={{
                            "& fieldset": { border: 'none' }
                        }}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment
                                    position="end"
                                    className="adornment">
                                    <Search
                                        className="text-color"
                                    />
                                </InputAdornment>
                            )
                        }}
                    />
                </Toolbar>
            </AppBar>
        </Box>
    )
}

export default MashupBar