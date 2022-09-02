import { ListItem, ListItemIcon, ListItemText } from '@mui/material';
import { Link } from 'react-router-dom';


export type ListItemRoutePayload = {
    toRoute: string
    label: string
    icon?: JSX.Element
}

const ListItemRoute = (payload: ListItemRoutePayload) => {

    return (
        <Link to={payload.toRoute} style={{ textDecoration: "none" }}>
            <ListItem button>
                {payload.icon &&
                    <ListItemIcon>{payload?.icon}</ListItemIcon>
                }
                <ListItemText primary={payload.label} />
            </ListItem>
        </Link>
    )

}

export default ListItemRoute