import * as React from 'react';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItem, { ListItemProps } from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MenuIcon from '@mui/icons-material/Menu';
import { Collapse, IconButton } from '@mui/material';

import MailIcon from '@mui/icons-material/Mail';
import SettingsIcon from '@mui/icons-material/Settings';
import { makeStyles } from '@mui/styles';
import clsx from 'clsx';
import './Drawer.css';
import { ExpandLess, ExpandMore, Label } from "@mui/icons-material";
import { Link } from 'react-router-dom';

const useStyles = makeStyles({
    list: {
        width: 250,
    },
    fullList: {
        width: 'auto',
    },
});

type Anchor = 'top' | 'left' | 'bottom' | 'right';


function ListItemRoute(props: { toRoute: string, label: string, button?: true }) {
    return (
        <Link to={props.toRoute} style={{textDecoration: "none"}}>
            <ListItem button>
                <ListItemText primary={props.label} />
            </ListItem>
        </Link>
    )
}

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

    const list = (anchor: Anchor) => (
        <div
            className={clsx(classes.list, {
                [classes.fullList]: anchor === 'top' || anchor === 'bottom',
            })}
            role="presentation"
        // onClick={toggleDrawer(anchor, false)}
        // onKeyDown={toggleDrawer(anchor, false)}
        >

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

                        <ListItemRoute label="Network" toRoute="/settings/network" />
                        <ListItemRoute label="My account" toRoute="/settings/my-account" />

                        <ListItem button>
                            <ListItemText primary="Libraries" />
                        </ListItem>
                        <ListItem button>
                            <ListItemText primary="Users" />
                        </ListItem>
                        <ListItem button>
                            <ListItemText primary="Groups" />
                        </ListItem>
                        <ListItem button>
                            <ListItemText primary="Encoding" />
                        </ListItem>
                    </List>

                </Collapse>
            </List>

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