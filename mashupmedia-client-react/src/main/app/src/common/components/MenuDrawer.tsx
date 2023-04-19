import { Collapse, ListItemButton } from '@mui/material';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
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
import { useAppDispatch } from "../redux/hooks";
import { userPolicy } from "../security/features/userPolicySlice";
import { redirectInternal } from "../utils/httpUtils";

export type MenuDrawerPayload = {
    openMenu: boolean
}

type InternalMenuDrawerPayload = {
    openMenu: boolean
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

const MenuDrawer = (menuDrawerPayload: MenuDrawerPayload) => {
    const classes = useStyles();

    const [props, setProps] = useState<InternalMenuDrawerPayload>({
        openMenu: false,
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

    useEffect(() => {


        setProps(({
            ...props,
            loggedIn: userPolicyPayload?.username ? true : false,
            administrator: userPolicyPayload?.administrator || false,
            openMenu: menuDrawerPayload.openMenu
        }))


    }, [userPolicyPayload])

    useEffect(() => {
        if (menuDrawerPayload.openMenu) {
            dispatch(
                userPolicy(tokenPayload)
            )
        }
        setProps(p => ({
            ...props,
            openMenu: menuDrawerPayload.openMenu
        }))

    }, [menuDrawerPayload])

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
        if (
            event.type === 'keydown' &&
            ((event as React.KeyboardEvent).key === 'Tab' ||
                (event as React.KeyboardEvent).key === 'Shift')
        ) {
            return;
        }

        setProps(p => ({
            ...props,
            openMenu: false
        }))

    }

    const closeAfterNavigate = () => {
        setProps(p => ({
            ...props,
            openMenu: false
        }))
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
                            <ListItemRoute label="Albums" toRoute="/music/albums" onClick={closeAfterNavigate} />
                            <ListItemRoute label="Artists" toRoute="/music/artists" onClick={closeAfterNavigate} />
                        </List>

                    </Collapse>
                </List>
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
                            <ListItemRoute label="Network" toRoute="/configuration/network" onClick={closeAfterNavigate} />
                            <ListItemRoute label="Encode" toRoute="/configuration/encode" onClick={closeAfterNavigate} />

                        </List>

                    </Collapse>
                </List>
            }

            {props.loggedIn &&
                <Divider />
            }
            {props.loggedIn &&
                <ListItemRoute label="Log out" toRoute="/logout" icon={<Logout />} onClick={handleLogOut} />
            }
            {!props.loggedIn &&
                <ListItemRoute label="Log in" toRoute="/login" icon={<Login />} onClick={closeAfterNavigate} />
            }
        </div>
    )

    return (
        <div className="Drawer" id="drawer-menu">
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
