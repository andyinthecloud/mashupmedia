import { Collapse } from '@mui/material';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { Fragment, useEffect, useState } from "react";

import { AccountBox, ExpandLess, ExpandMore, LibraryMusic, Login, Logout } from "@mui/icons-material";
import SettingsIcon from '@mui/icons-material/Settings';
import { makeStyles } from '@mui/styles';
import clsx from 'clsx';
import { useSelector } from "react-redux";
import { RootState } from '../redux/store';
import ListItemRoute from './ListItemRoute';
import './MenuDrawer.css';


export type MenuDrawerPayload = {
    openMenu: boolean
}

type SubMenuPayload = {
    musicMenuOpen: boolean
    settingsMenuOpen: boolean
}


const useStyles = makeStyles({
    list: {
        width: 250,
    },
    fullList: {
        width: 'auto',
    },
});

type Anchor = 'top' | 'left' | 'bottom' | 'right';

enum MenuType {
    MUSIC, SETTINGS
}


const MenuDrawer = (payload: MenuDrawerPayload) => {
    const classes = useStyles();
    
    const [props, setProps] = useState<MenuDrawerPayload>({
        openMenu: false
    })

    const [subMenuPayload, setSubMenuPayload] = useState<SubMenuPayload>({
        musicMenuOpen: false,
        settingsMenuOpen: false
    })


    useEffect(() => {
        setProps(payload)
    }, [payload])
    
    const handleMenuClick = (menuType: MenuType): void => {

        switch (menuType) {
            case MenuType.MUSIC:
                setSubMenuPayload(p => ({
                    ...p,
                    musicMenuOpen: !subMenuPayload.musicMenuOpen
                }))
                break;

            case MenuType.SETTINGS:
                setSubMenuPayload(p => ({
                    ...p,
                    settingsMenuOpen: !subMenuPayload.settingsMenuOpen
                }))
                break;
        }
    }


    const closeDrawer = () => (
        event: React.KeyboardEvent | React.MouseEvent,
    ) => {

        if (
            event.type === 'keydown' &&
            ((event as React.KeyboardEvent).key === 'Tab' ||
                (event as React.KeyboardEvent).key === 'Shift')
        ) {
            return;
        }

        setProps({
            openMenu: false
        })

    };

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const list = (anchor: Anchor) => (
        <div
            className={clsx(classes.list, {
                [classes.fullList]: anchor === 'top' || anchor === 'bottom',
            })}
            role="presentation"
        >

            <ListItemRoute label="My account" toRoute="/configuration/my-account" icon={<AccountBox />} />
            <Divider />

            {userPolicyPayload &&
                <List>
                    <ListItem button onClick={() => handleMenuClick(MenuType.MUSIC)}>
                        <ListItemIcon>
                            <LibraryMusic />
                        </ListItemIcon>
                        <ListItemText primary="Music" />
                        {subMenuPayload.musicMenuOpen ? <ExpandLess /> : <ExpandMore />}
                    </ListItem>
                    <Collapse in={subMenuPayload.musicMenuOpen} timeout="auto" unmountOnExit>
                        <List component="div" disablePadding className="nested-list">
                            <ListItemRoute label="Albums" toRoute="/music/albums" />
                            <ListItemRoute label="Artists" toRoute="/music/artists" />

                        </List>

                    </Collapse>
                </List>
            }
            <Divider />

            {userPolicyPayload && userPolicyPayload.administrator &&
                <List>
                    <ListItem button onClick={() => handleMenuClick(MenuType.SETTINGS)}>
                        <ListItemIcon>
                            <SettingsIcon />
                        </ListItemIcon>
                        <ListItemText primary="Settings" />
                        {subMenuPayload.settingsMenuOpen ? <ExpandLess /> : <ExpandMore />}
                    </ListItem>
                    <Collapse in={subMenuPayload.settingsMenuOpen} timeout="auto" unmountOnExit>
                        <List component="div" disablePadding className="nested-list">

                            <ListItemRoute label="Users" toRoute="/configuration/users" />
                            <ListItemRoute label="Groups" toRoute="/configuration/groups" />
                            <ListItemRoute label="Libraries" toRoute="/configuration/libraries" />
                            <ListItemRoute label="Network" toRoute="/configuration/network" />
                            <ListItem button>
                                <ListItemText primary="Encoding" />
                            </ListItem>

                        </List>

                    </Collapse>
                </List>
            }

            <Divider />
            {userPolicyPayload &&
                <ListItemRoute label="Log out" toRoute="/logout" icon={<Logout />} />
            }
            {!userPolicyPayload &&
                <ListItemRoute label="Log in" toRoute="/login" icon={<Login />} />
            }

        </div>
    );

    return (
        <div className="Drawer" id="drawer-menu">

            {/* <IconButton onClick={toggleDrawer('right', true)} className="menu-icon">
                <MenuIcon />
            </IconButton> */}

            {(['left'] as Anchor[]).map((anchor) => (
                <Fragment key={anchor}>

                    <Drawer anchor={anchor} open={props.openMenu} onClose={closeDrawer()}>
                        {list(anchor)}
                    </Drawer>
                </Fragment>
            ))}
        </div>
    );
}

export default MenuDrawer
