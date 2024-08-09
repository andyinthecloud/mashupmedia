import { Album, Audiotrack, Edit, MoreVert } from "@mui/icons-material"
import { IconButton, Menu, MenuItem } from "@mui/material"
import { t } from "i18next"
import { useEffect, useState } from "react"
import './MusicMetaMenu.css'
import { useNavigate } from "react-router-dom"

export type MusicMetaMenuPagePayload = {
    editor: boolean
    editLabel: string,
    artistId: number,
    albumId?: number,
    edit(): void
    uploadTracks(): void
    addAlbum(): void
}

type InternalMusicMetaMenuPagePayload = {
    anchorElement?: HTMLElement
    musicMetaMenuPagePayload: MusicMetaMenuPagePayload
}

const MusicMetaMenu = (payload: MusicMetaMenuPagePayload) => {
    const navigate = useNavigate()

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

    useEffect(() => {
        setProps(p => ({
            ...p,
            musicMetaMenuPagePayload: {
                ...p.musicMetaMenuPagePayload,
                editor: payload.editor,
                artistId: payload.artistId 
            }
        }))

    }, [payload.artistId])

    useEffect(() => {
        setProps(p => ({
            ...p,
            musicMetaMenuPagePayload: {
                ...p.musicMetaMenuPagePayload,
                editor: payload.editor,
                albumId: payload.albumId 
            }
        }))

    }, [payload.albumId])


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

    function handleClickEdit(): void {
        props.musicMetaMenuPagePayload.edit()
        closeMenu()
    }

    function handleClickAddAlbum(): void {
        props.musicMetaMenuPagePayload.addAlbum()
        closeMenu()
    }


    function handleClickUploadTracks(): void {

        const artistId = props.musicMetaMenuPagePayload.artistId
        const albumId = props.musicMetaMenuPagePayload.albumId || undefined

        navigate('/music/upload-artist-tracks', {
            state: {
                artistId,
                albumId
            }
        })

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