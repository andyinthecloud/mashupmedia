import { ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import { MouseEventHandler } from "react";
import { Link } from 'react-router-dom';
import './ListItemRoute.css';


export interface ListItemRoutePayload {
    toRoute: string
    label: string
    icon?: JSX.Element
    onClick?: MouseEventHandler<HTMLDivElement>
}

const ListItemRoute = (payload: ListItemRoutePayload) => {


    return (
        <Link to={payload.toRoute} style={{ textDecoration: "none" }} id="list-item-route">
            <ListItemButton                
                onClick={payload.onClick}>
                {payload.icon &&
                    <ListItemIcon >{payload?.icon}</ListItemIcon>
                }
                <ListItemText primary={payload.label}/>
            </ListItemButton>
        </Link>
    )

}

export default ListItemRoute