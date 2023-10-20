import ListIcon from '@mui/icons-material/List';
import { Collapse, ListItemButton, ListItemIcon } from '@mui/material';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItemText from '@mui/material/ListItemText';
import { Fragment, useEffect, useState } from "react";

import { AccountBox, ExpandLess, ExpandMore, LibraryMusic, Login, Logout, PersonAdd, Search } from "@mui/icons-material";
import SettingsIcon from '@mui/icons-material/Settings';
import { makeStyles } from '@mui/styles';
import clsx from 'clsx';
import { useSelector } from "react-redux";
import { useAppDispatch } from "../redux/hooks";
import { RootState } from '../redux/store';
import { userPolicy } from "../security/features/userPolicySlice";
import { redirectInternal } from "../utils/httpUtils";
import ListItemRoute from './ListItemRoute';
import './MenuDrawer.css';
import { closeMenu } from "./features/menuSlice";


type InternalMenuDrawerPayload = {
    loggedIn: boolean
    administrator: boolean
    internalSubMenuPayload: InternalSubMenuPayload
}

type InternalSubMenuPayload = {
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

const MenuDrawer = () => {
    const classes = useStyles();

    const [props, setProps] = useState<InternalMenuDrawerPayload>({
        loggedIn: false,
        administrator: false,
        internalSubMenuPayload: {
            musicMenuOpen: false,
            settingsMenuOpen: false
        }
    })

    const dispatch = useAppDispatch()

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)
    const tokenPayload = useSelector((state: RootState) => state.security.payload?.token)
    const menuPayload = useSelector((state: RootState) => state.menuState)

    useEffect(() => {
        console.log("useEffect: userPolicyPayload", userPolicyPayload)
        setProps(({
            ...props,
            loggedIn: userPolicyPayload?.username ? true : false,
            administrator: userPolicyPayload?.administrator || false
        }))


    }, [userPolicyPayload])


    useEffect(() => {
        dispatch(
            userPolicy(tokenPayload)
        )

    }, [tokenPayload])



    const setSubMenuPayloadProps = (subMenuPayload: InternalSubMenuPayload): void => {
        setProps(p => ({
            ...p,
            internalSubMenuPayload: {
                ...subMenuPayload
            }
        }))
    }

    const handleMenuClick = (menuType: MenuType): void => {
        switch (menuType) {
            case MenuType.MUSIC:
                setSubMenuPayloadProps({
                    ...props.internalSubMenuPayload,
                    musicMenuOpen: !props.internalSubMenuPayload.musicMenuOpen
                })
                break

            case MenuType.SETTINGS:
                setSubMenuPayloadProps({
                    ...props.internalSubMenuPayload,
                    settingsMenuOpen: !props.internalSubMenuPayload.settingsMenuOpen
                })
                break
        }
    }

    const closeDrawer = () => (event: React.KeyboardEvent | React.MouseEvent) => {

        console.log("closeDrawer")

        if (
            event.type === 'keydown' &&
            ((event as React.KeyboardEvent).key === 'Tab' ||
                (event as React.KeyboardEvent).key === 'Shift')
        ) {
            return;
        }

        dispatch(
            closeMenu()
        )

    }

    const isOpen = (): boolean => {
        return menuPayload.isOpen
    }

    const closeAfterNavigate = () => {
        dispatch(
            closeMenu()
        )
    }



    const handleLogOut = () => {
        redirectInternal('/logout')
    }

    const list = (anchor: Anchor) => (
        <div
            className={clsx(classes.list, {
                [classes.fullList]: anchor === 'top' || anchor === 'bottom',
            })}
            role="presentation"
        >

            {props.loggedIn &&
                <ListItemRoute label="My account" toRoute="/configuration/my-account" icon={<AccountBox />} />
            }

            {props.loggedIn &&
                <Divider />
            }

            {props.loggedIn &&
                <List>
                    <ListItemButton onClick={() => handleMenuClick(MenuType.MUSIC)}>
                        <ListItemIcon>
                            <LibraryMusic />
                        </ListItemIcon>
                        <ListItemText primary="Music" />
                        {props.internalSubMenuPayload.musicMenuOpen ? <ExpandLess /> : <ExpandMore />}
                    </ListItemButton>
                    <Collapse in={props.internalSubMenuPayload.musicMenuOpen} timeout="auto" unmountOnExit>
                        <List
                            component="div"
                            disablePadding
                            className="nested-list">
                            <ListItemRoute label="Albums" toRoute="/music/albums" onClick={() => closeAfterNavigate()} />
                            <ListItemRoute label="Artists" toRoute="/music/artists" onClick={() => closeAfterNavigate()} />
                        </List>

                    </Collapse>
                </List>
            }
            {props.loggedIn &&
                <Divider />
            }

            {props.loggedIn &&
                <ListItemRoute label="Playlists" toRoute="/playlists/all" icon={<ListIcon />} onClick={() => closeAfterNavigate()} />
            }

            {props.administrator &&
                <Divider />
            }

            {props.administrator &&
                <List>
                    <ListItemButton onClick={() => handleMenuClick(MenuType.SETTINGS)}>
                        <ListItemIcon>
                            <SettingsIcon />
                        </ListItemIcon>
                        <ListItemText primary="Settings" />
                        {props.internalSubMenuPayload.settingsMenuOpen ? <ExpandLess /> : <ExpandMore />}
                    </ListItemButton>
                    <Collapse in={props.internalSubMenuPayload.settingsMenuOpen} timeout="auto" unmountOnExit>
                        <List
                            component="div"
                            disablePadding
                            className="nested-list">

                            <ListItemRoute label="Users" toRoute="/configuration/users" onClick={closeAfterNavigate} />
                            <ListItemRoute label="Groups" toRoute="/configuration/groups" onClick={closeAfterNavigate} />
                            <ListItemRoute label="Libraries" toRoute="/configuration/libraries" onClick={closeAfterNavigate} />
                            <ListItemRoute label="Encode" toRoute="/configuration/encode" onClick={closeAfterNavigate} />

                        </List>

                    </Collapse>
                </List>
            }

            {props.loggedIn &&
                <Divider />
            }

            {props.loggedIn &&
                <ListItemRoute label="Search" toRoute="/search/media" icon={<Search />} onClick={() => closeAfterNavigate()} />
            }

            {props.administrator &&
                <Divider />
            }

            {props.loggedIn &&
                <ListItemRoute label="Log out" toRoute="/logout" icon={<Logout />} onClick={handleLogOut} />
            }
            {!props.loggedIn &&
                <List>
                    <ListItemRoute label="Log in" toRoute="/login" icon={<Login />} onClick={closeAfterNavigate} />
                    <ListItemRoute label="New user" toRoute="/new-account" icon={<PersonAdd />} onClick={closeAfterNavigate} />
                </List>
            }
        </div>
    )

    return (
        <div className="Drawer" id="drawer-menu">
            {(['left'] as Anchor[]).map((anchor) => (
                <Fragment key={anchor}>
                    <Drawer anchor={anchor} open={isOpen()} onClose={closeDrawer()}>
                        {list(anchor)}
                    </Drawer>
                </Fragment>
            ))}
        </div>
    );
}

export default MenuDrawer
