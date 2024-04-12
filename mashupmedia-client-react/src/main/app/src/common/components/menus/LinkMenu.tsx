import { Delete, Edit, KeyboardArrowDown, KeyboardArrowUp, KeyboardDoubleArrowDown, KeyboardDoubleArrowUp } from "@mui/icons-material"
import { Menu, MenuItem } from "@mui/material"
import { useEffect, useState } from "react"
import { ExternalLinkPayload } from "../../../media/rest/mediaCalls"
import './LinkMenu.css'

export type LinkMenuPayload = {
    open: boolean
    externalLinkPayload?: ExternalLinkPayload
    anchorElement: HTMLElement | null
    editLink: (externalLinkPayload: ExternalLinkPayload) => void
    deleteLink: (externalLinkPayload: ExternalLinkPayload) => void
    moveTop: (externalLinkPayload: ExternalLinkPayload) => void
    moveUpOne: (externalLinkPayload: ExternalLinkPayload) => void
    moveDownOne: (externalLinkPayload: ExternalLinkPayload) => void
    moveBottom: (externalLinkPayload: ExternalLinkPayload) => void    
}




const LinkMenu = (payload: LinkMenuPayload) => {

    const [props, setProps] = useState<LinkMenuPayload>(payload)

    useEffect(() => {
        setProps(payload)

    }, [payload])


    const handleEditExternalLink = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.editLink(props.externalLinkPayload)
    }

    const handleDeleteExternalLink = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.deleteLink(props.externalLinkPayload)
    }

    const handleMoveTop = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.moveTop(props.externalLinkPayload)
    }

    const handleMoveUpOne = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.moveUpOne(props.externalLinkPayload)
    }

    const handleMoveDownOne = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.moveDownOne(props.externalLinkPayload)
    }

    const handleMoveBottom = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.moveBottom(props.externalLinkPayload)
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
                onClick={handleEditExternalLink}>
                <Edit />
                Edit
            </MenuItem>
            <MenuItem
                onClick={handleDeleteExternalLink}>
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