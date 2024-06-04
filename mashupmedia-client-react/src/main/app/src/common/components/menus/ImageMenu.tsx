import { Delete, Edit, KeyboardArrowDown, KeyboardArrowUp, KeyboardDoubleArrowDown, KeyboardDoubleArrowUp, Preview } from "@mui/icons-material"
import { Menu, MenuItem } from "@mui/material"
import { useEffect, useState } from "react"
import { MenuMetaPayload } from "../../../media/rest/mediaCalls"
import { MetaImagePayload } from "../../../media/music/rest/musicUploadCalls"

const ImageMenu = (menuMetaPayload: MenuMetaPayload<MetaImagePayload>) => {

    const [props, setProps] = useState<MenuMetaPayload<MetaImagePayload>>(menuMetaPayload)

    useEffect(() => {
        setProps(menuMetaPayload)

    }, [menuMetaPayload])

    const handlePreview = (): void => {
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

    const handleMakeDefault = (): void => {
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
            open: false
        }))
    }


    return (
        <Menu
            id="link-menu"
            open={props.open}
            anchorEl={props.anchorElement}
            onClose={handleClose}>
            <MenuItem
                onClick={handlePreview}>
                <Preview />
                Preview
            </MenuItem>
            <MenuItem
                onClick={handleDelete}>
                <Delete />
                Delete
            </MenuItem>
            <MenuItem
                onClick={handleMakeDefault}>
                <KeyboardDoubleArrowUp />
                Make default
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
                Move end
            </MenuItem>
        </Menu>
    )
}

export default ImageMenu