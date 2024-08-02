import { AddAPhoto, AddLink, MoreVert } from "@mui/icons-material"
import { IconButton, Menu, MenuItem } from "@mui/material"
import { t } from "i18next"
import { useState } from "react"

export type MusicEditMenuPayload = {
    addImage(): void
    addExternalLink(): void
}

type InternalMusicEditMenu = {
    anchorElement?: HTMLElement
    musicEditMenuPayload: MusicEditMenuPayload
}

const MusicEditMenu = (payload: MusicEditMenuPayload) => {

    const [props, setProps] = useState<InternalMusicEditMenu>({
        musicEditMenuPayload: payload
    })

    function handleClose(): void {
        closeMenu()
    }

    function closeMenu(): void {
        setProps(p => ({
            ...p,
            anchorElement: undefined
        }))
    }

    function isMenuOpen(): boolean {
        return props.anchorElement ? true : false
    }

    function handleClickAddImage(): void {
        props.musicEditMenuPayload.addImage()
        closeMenu()
    }

    function handleClickAddLink(): void {
        props.musicEditMenuPayload.addExternalLink()
        closeMenu()
    }

    function handleOpenMenu(event: React.MouseEvent<HTMLElement>): void {
        setProps(p => ({
            ...p,
            anchorElement: event.currentTarget
        }))
    }

    return (
        <div id="music-edit-menu">

            <IconButton
                color="primary"
                onClick={handleOpenMenu}>
                <MoreVert />
            </IconButton>


            <Menu
                id="music-meta-menu"
                open={isMenuOpen()}
                onClose={handleClose}
                anchorEl={props.anchorElement}
            >
                <MenuItem
                    onClick={handleClickAddImage}>
                    <AddAPhoto />
                    {t('menu.musicMeta.addImage')}
                </MenuItem>
                <MenuItem
                    onClick={handleClickAddLink}>
                    <AddLink />
                    {t('menu.musicMeta.addLink')}
                </MenuItem>
            </Menu>

        </div>
    )
}

export default MusicEditMenu