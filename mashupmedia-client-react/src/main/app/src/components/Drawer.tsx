import MenuIcon from '@mui/icons-material/Menu';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import { Collapse, IconButton } from '@mui/material';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import * as React from 'react';

import { AccountBox, ExpandLess, ExpandMore, Login } from "@mui/icons-material";
import MailIcon from '@mui/icons-material/Mail';
import SettingsIcon from '@mui/icons-material/Settings';
import { makeStyles } from '@mui/styles';
import clsx from 'clsx';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/store';
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
        // onClick={toggleDrawer(anchor, false)}
        // onKeyDown={toggleDrawer(anchor, false)}
        >
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

                            <ListItemRoute label="Users" toRoute="/settings/users" />
                            <ListItemRoute label="Groups" toRoute="/settings/groups" />

                            <ListItem button>
                                <ListItemText primary="Libraries" />
                            </ListItem>
                            <ListItemRoute label="Network" toRoute="/settings/network" />
                            <ListItem button>
                                <ListItemText primary="Encoding" />
                            </ListItem>

                        </List>

                    </Collapse>
                </List>
            }

            <ListItemRoute label="My account" toRoute="/settings/my-account" icon={<AccountBox />} />
            <Divider />
            {userPolicyPayload &&
                <ListItemRoute label="Log out" toRoute="/login" />
            }
            {!userPolicyPayload &&
                <ListItemRoute label="Log in" toRoute="/login" icon={<Login />} />
            }
            <Divider />


            <List>
                {['Inbox', 'Starred', 'Send email', 'Drafts'].map((text, index) => (
                    <ListItem button key={text}>
                        <ListItemIcon>{index % 2 === 0 ? <InboxIcon /> : <MailIcon />}</ListItemIcon>
                        <ListItemText primary={text} />
                    </ListItem>
                ))}
            </List>
            <Divider />
            <List>
                {['All mail', 'Trash', 'Spam'].map((text, index) => (
                    <ListItem button key={text}>
                        <ListItemIcon>{index % 2 === 0 ? <InboxIcon /> : <MailIcon />}</ListItemIcon>
                        <ListItemText primary={text} />
                    </ListItem>
                ))}
            </List>
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