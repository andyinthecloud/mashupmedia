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
}




const LinkMenu = (payload: LinkMenuPayload) => {

    const [props, setProps] = useState<LinkMenuPayload>(payload)

    useEffect(() => {
        setProps(payload)

    }, [payload])


    const handleEditExternalLLInk = (): void => {
        if (!props.externalLinkPayload) {
            return
        }

        props.editLink(props.externalLinkPayload)
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
                onClick={handleEditExternalLLInk}>
                <Edit />
                Edit
            </MenuItem>
            <MenuItem>
                <Delete />
                Delete
            </MenuItem>
            <MenuItem>
                <KeyboardDoubleArrowUp />
                Move top
            </MenuItem>
            <MenuItem>
                <KeyboardArrowUp />
                Move up one
            </MenuItem>
            <MenuItem>
                <KeyboardArrowDown />
                Move down one
            </MenuItem>
            <MenuItem>
                <KeyboardDoubleArrowDown />
                Move bottom
            </MenuItem>
        </Menu>
    )

}

export default LinkMenu