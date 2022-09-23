import MenuIcon from '@mui/icons-material/Menu';
import { Collapse, IconButton } from '@mui/material';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import * as React from 'react';

import { AccountBox, ExpandLess, ExpandMore, Login, Logout } from "@mui/icons-material";
import SettingsIcon from '@mui/icons-material/Settings';
import { makeStyles } from '@mui/styles';
import clsx from 'clsx';
import { useSelector } from 'react-redux';
import { RootState } from '../../common/redux/store';
import './Drawer.css';
import ListItemRoute from './ListItemRoute';

const useStyles = makeStyles({
    list: {
        width: 250,
    },
    fullList: {
        width: 'auto',
    },
});

type Anchor = 'top' | 'left' | 'bottom' | 'right';


export default function TemporaryDrawer() {
    const classes = useStyles();
    const [state, setState] = React.useState({
        top: false,
        left: false,
        bottom: false,
        right: false,
    });

    const [open, setOpen] = React.useState(false);
    const handleClick = () => {
        setOpen(!open);
    };

    const toggleDrawer = (anchor: Anchor, open: boolean) => (
        event: React.KeyboardEvent | React.MouseEvent,
    ) => {
        if (
            event.type === 'keydown' &&
            ((event as React.KeyboardEvent).key === 'Tab' ||
                (event as React.KeyboardEvent).key === 'Shift')
        ) {
            return;
        }

        setState({ ...state, [anchor]: open });
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


            {userPolicyPayload && userPolicyPayload.administrator &&
                <List>
                    <ListItem button onClick={handleClick}>
                        <ListItemIcon>
                            <SettingsIcon></SettingsIcon>
                        </ListItemIcon>
                        <ListItemText primary="Settings" />
                        {open ? <ExpandLess /> : <ExpandMore />}
                    </ListItem>
                    <Collapse in={open} timeout="auto" unmountOnExit>
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
        <div className="Drawer">

            <IconButton onClick={toggleDrawer('right', true)}>
                <MenuIcon />
            </IconButton>

            {(['right'] as Anchor[]).map((anchor) => (
                <React.Fragment key={anchor}>

                    <Drawer anchor={anchor} open={state[anchor]} onClose={toggleDrawer(anchor, false)}>
                        {list(anchor)}
                    </Drawer>
                </React.Fragment>
            ))}
        </div>
    );
}