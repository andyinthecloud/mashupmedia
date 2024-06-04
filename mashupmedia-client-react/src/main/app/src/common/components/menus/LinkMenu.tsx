import { Delete, Edit, KeyboardArrowDown, KeyboardArrowUp, KeyboardDoubleArrowDown, KeyboardDoubleArrowUp } from "@mui/icons-material"
import { Menu, MenuItem } from "@mui/material"
import { useEffect, useState } from "react"
import { ExternalLinkPayload, MenuMetaPayload } from "../../../media/rest/mediaCalls"
import './LinkMenu.css'

const LinkMenu = (menuMetaPayload: MenuMetaPayload<ExternalLinkPayload>) => {

    const [props, setProps] = useState<MenuMetaPayload<ExternalLinkPayload>>(menuMetaPayload)

    useEffect(() => {
        setProps(menuMetaPayload)

    }, [menuMetaPayload])


    const handleEdit = (): void => {
        if (!props.payload) {
            return
        }

        props.edit(props.payload)
    }

    const handleDelete = (): void => {
        if (!props.payload) {
            return
        }
        
        props.delete(props.payload)
    }

    const handleMoveTop = (): void => {
        if (!props.payload) {
            return
        }

        props.moveTop(props.payload)
    }

    const handleMoveUpOne = (): void => {
        if (!props.payload) {
            return
        }

        props.moveUpOne(props.payload)
    }

    const handleMoveDownOne = (): void => {
        if (!props.payload) {
            return
        }

        props.moveDownOne(props.payload)
    }

    const handleMoveBottom = (): void => {
        if (!props.payload) {
            return
        }

        props.moveBottom(props.payload)
    }

    const handleClose = (): void => {
        setProps(p => ({
            ...p,
            anchorElement: null,
            open: false,
            externalLinkPayload: undefined
        }))
    }

    return (
        <Menu
            id="link-menu"
            open={props.open}
            anchorEl={props.anchorElement}
            onClose={handleClose}>
            <MenuItem
                onClick={handleEdit}>
                <Edit />
                Edit
            </MenuItem>
            <MenuItem
                onClick={handleDelete}>
                <Delete />
                Delete
            </MenuItem>
            <MenuItem
                onClick={handleMoveTop}>
                <KeyboardDoubleArrowUp />
                Move top
            </MenuItem>
            <MenuItem
                onClick={handleMoveUpOne}>
                <KeyboardArrowUp />
                Move up one
            </MenuItem>
            <MenuItem
                onClick={handleMoveDownOne}>
                <KeyboardArrowDown />
                Move down one
            </MenuItem>
            <MenuItem
                onClick={handleMoveBottom}>
                <KeyboardDoubleArrowDown />
                Move bottom
            </MenuItem>
        </Menu>
    )

}

export default LinkMenu