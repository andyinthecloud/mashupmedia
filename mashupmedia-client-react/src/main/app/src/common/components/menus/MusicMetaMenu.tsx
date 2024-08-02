import { Album, Audiotrack, Edit, MoreVert } from "@mui/icons-material"
import { IconButton, Menu, MenuItem } from "@mui/material"
import { t } from "i18next"
import { useEffect, useState } from "react"
import './MusicMetaMenu.css'

export type MusicMetaMenuPagePayload = {
    editor: boolean
    editLabel: string,
    edit(): void
    uploadTracks(): void
    addAlbum(): void

    // artistId: number
    // editName(): void
    // editSummary(): void
    // addImage(): void
    // addExternalLink(): void
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

    // useEffect(() => {
    //     setProps(p => ({
    //         ...p,
    //         musicMetaMenuPagePayload: {
    //             ...p.musicMetaMenuPagePayload,
    //             artistId: payload.artistId
    //         }
    //     }))

    // }, [payload.artistId])

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

    // function handleClickEditName(): void {
    //     props.musicMetaMenuPagePayload.editName()
    //     closeMenu()
    // }


    // function handleClickEditSummary(): void {
    //     props.musicMetaMenuPagePayload.editSummary()
    //     closeMenu()
    // }

    // function handleClickAddImage(): void {
    //     props.musicMetaMenuPagePayload.addImage()
    //     closeMenu()
    // }

    // function handleClickAddLink(): void {
    //     props.musicMetaMenuPagePayload.addExternalLink()
    //     closeMenu()
    // }

    function handleClickEdit(): void {
        props.musicMetaMenuPagePayload.edit()
        closeMenu()
    }

    function handleClickAddAlbum(): void {
        props.musicMetaMenuPagePayload.addAlbum()
        closeMenu()
    }


    function handleClickUploadTracks(): void {
        // navigate('/music/upload-artist-tracks/' + props.musicMetaMenuPagePayload.artistId)
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
                    {/* <MenuItem
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
                    </MenuItem> */}

<MenuItem
                        onClick={handleClickEdit}>
                        <Edit />
                        {props.musicMetaMenuPagePayload.editLabel}
                    </MenuItem>

                    <MenuItem
                        onClick={handleClickAddAlbum}>
                        <Album />
                        {t('menu.musicMeta.addAlbum')}
                    </MenuItem>
                    <MenuItem
                        onClick={handleClickUploadTracks}>
                        <Audiotrack />
                        {t('menu.musicMeta.uploadArtistTracks')}
                    </MenuItem>
                </Menu>
            }

        </div>

    )
}

export default MusicMetaMenu