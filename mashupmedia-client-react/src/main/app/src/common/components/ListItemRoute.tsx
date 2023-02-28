import { ReactJSXElement } from "@emotion/react/types/jsx-namespace";
import { ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import { DOMAttributes, MouseEventHandler } from "react";
import { Link } from 'react-router-dom';


export interface ListItemRoutePayload {
    toRoute: string
    label: string
    icon?: JSX.Element
    onClick?: MouseEventHandler<HTMLDivElement>
}

const ListItemRoute = (payload: ListItemRoutePayload) => {


    return (
        <Link to={payload.toRoute} style={{ textDecoration: "none" }}>
            <ListItemButton
                onClick={payload.onClick}>
                {payload.icon &&
                    <ListItemIcon>{payload?.icon}</ListItemIcon>
                }
                <ListItemText primary={payload.label} />
            </ListItemButton>
        </Link>
    )

}

export default ListItemRoute