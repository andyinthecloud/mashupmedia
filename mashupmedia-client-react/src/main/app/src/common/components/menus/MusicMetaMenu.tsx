import { AddAPhoto, AddLink, Album, Edit, MoreVert } from "@mui/icons-material"
import { IconButton, Menu, MenuItem } from "@mui/material"
import { t } from "i18next"
import { useEffect, useState } from "react"
import './MusicMetaMenu.css'

export type MusicMetaMenuPagePayload = {
    editor: boolean
    editName(): void
    editSummary(): void
    addImage(): void
    addExternalLink(): void
    addAlbum(): void
}

type InternalMusicMetaMenuPagePayload = {
    anchorElement?: HTMLElement
    musicMetaMenuPagePayload: MusicMetaMenuPagePayload
}

const MusicMetaMenu = (payload: MusicMetaMenuPagePayload) => {

    const [props, setProps] = useState<InternalMusicMetaMenuPagePayload>({
        musicMetaMenuPagePayload: payload
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            musicMetaMenuPagePayload: {
                ...p.musicMetaMenuPagePayload,
                editor: payload.editor
            }
        }))

    }, [payload.editor])

    function handleClose(): void {
        closeMenu()
    }

    function closeMenu(): void {
        setProps(p => ({
            ...p,
            anchorElement: undefined
        }))
    }

    function handleOpenMenu(event: React.MouseEvent<HTMLElement>): void {
        setProps(p => ({
            ...p,
            anchorElement: event.currentTarget
        }))
    }

    function handleClickEditName(): void {
        props.musicMetaMenuPagePayload.editName()
        closeMenu()
    }


    function handleClickEditSummary(): void {
        props.musicMetaMenuPagePayload.editSummary()
        closeMenu()
    }

    function handleClickAddImage(): void {
        props.musicMetaMenuPagePayload.addImage()
        closeMenu()
    }

    function handleClickAddLink(): void {
        props.musicMetaMenuPagePayload.addExternalLink()
        closeMenu()
    }

    function handleClickAddAlbum(): void {
        props.musicMetaMenuPagePayload.addAlbum()
        closeMenu()
    }

    function isMenuOpen(): boolean {
        return props.anchorElement ? true : false
    }

    return (
        <div>
            {props.musicMetaMenuPagePayload.editor &&
                <IconButton
                    color="primary"
                    onClick={handleOpenMenu}>
                    <MoreVert />
                </IconButton>
            }

            {props.musicMetaMenuPagePayload.editor &&
                <Menu
                    id="music-meta-menu"
                    open={isMenuOpen()}
                    onClose={handleClose}
                    anchorEl={props.anchorElement}
                >
                    <MenuItem
                        onClick={handleClickEditName}>
                        <Edit />
                        {t('menu.musicMeta.editName')}
                    </MenuItem>
                    <MenuItem
                        onClick={handleClickEditSummary}>
                        <Edit />
                        {t('menu.musicMeta.editSummary')}
                    </MenuItem>
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
                    <MenuItem
                        onClick={handleClickAddAlbum}>
                        <Album />
                        {t('menu.musicMeta.addAlbum')}
                    </MenuItem>
                </Menu>
            }

        </div>

    )
}

export default MusicMetaMenu