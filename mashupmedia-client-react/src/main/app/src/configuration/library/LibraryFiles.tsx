import { Article, Folder, Image, MoreVert, Movie, MusicNote } from "@mui/icons-material"
import { Breadcrumbs, IconButton, Link, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material"
import React, { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import { LibraryPayload, LibraryTypePayload } from "../backend/libraryCalls"
import { LibraryFilePayload, LibraryFilesPayload, getLibraryFiles } from "../backend/libraryFileCalls"
import { LibraryPagePayload, TabPanelPayload } from "./Library"
import LibraryFileMenu, { LibraryFileMenuPayload } from "./LibraryFileMenu"
import './LibraryFiles.css'
import { MashupMediaType } from "../../media/music/rest/playlistActionCalls"


type LibraryFilesPagePayload = {
    libraryPayload: LibraryPayload
    tabPanelPayload: TabPanelPayload
    uploadFileList?: FileList
    libraryFilesPayload?: LibraryFilesPayload
    uploadDisabled: boolean
    folderPath?: string
    libraryFolderMenuPayload: LibraryFileMenuPayload

}

const LibraryFiles = (libraryPagePayload: LibraryPagePayload) => {

    const tabIndex = 1;

    const userToken = useSelector((state: RootState) => state.security.payload?.token)


    const [props, setProps] = useState<LibraryFilesPagePayload>({
        libraryPayload: {
            name: '',
            path: '',
            enabled: true,
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        tabPanelPayload: {
            index: tabIndex
        },
        uploadDisabled: true,
        libraryFolderMenuPayload: {
            anchorElement: null,
            enableUpload: false
        }
    })

    useEffect(() => {
        const libraryId = libraryPagePayload.libraryPayload.id
        if (!libraryId) {
            return
        }

        getLibraryFiles(libraryId, props.folderPath, userToken).then(response => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    libraryFilesPayload: response.parsedBody,
                    libraryPayload: libraryPagePayload.libraryPayload,
                    tabPanelPayload: {
                        index: tabIndex,
                        value: libraryPagePayload.tabPanelPayload.value
                    },
                    libraryFolderMenuPayload: {
                        anchorElement: null,
                        enableUpload: false,
                        libraryId: libraryPagePayload.libraryPayload.id,
                        path: libraryPagePayload.libraryPayload.path
                    }
                }))
            }
        })

    }, [libraryPagePayload])


    const handleClickLibraryFile = (folderPath: string): void => {
        const libraryId = props.libraryPayload.id
        if (!libraryId) {
            return
        }

        getLibraryFiles(libraryId, folderPath, userToken).then(response => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    folderPath,
                    libraryFilesPayload: response.parsedBody,
                    libraryFolderMenuPayload: {
                        anchorElement: null,
                        libraryId,
                        enableUpload: false
                    }
                }))
            }
        })
    }

    const handleMoreFileClick = (event: React.MouseEvent<HTMLElement>, path: string, enableUpload: boolean): void => {
        setProps(p => ({
            ...p,
            libraryFolderMenuPayload: {
                ...p.libraryFolderMenuPayload,
                anchorElement: event.currentTarget,
                enableUpload,
                path
            }
        }))
    }

    const renderFolder = (libraryFilePayload: LibraryFilePayload) => {
        return (
            <ListItem
                key={libraryFilePayload.name}
                secondaryAction={
                    <IconButton
                        edge="end"
                        onClick={(e) => handleMoreFileClick(e, libraryFilePayload.path, false)}>
                        <MoreVert />
                    </IconButton>
                }
            >
                <ListItemButton
                    onClick={() => handleClickLibraryFile(libraryFilePayload.path)}>
                    <ListItemIcon>
                        <Folder />
                    </ListItemIcon>
                    <ListItemText primary={libraryFilePayload.name} />
                </ListItemButton>
            </ListItem>
        )
    }

    const renderFile = (libraryFilePayload: LibraryFilePayload) => {
        return (
            <ListItem
                key={libraryFilePayload.name}
                secondaryAction={
                    <IconButton
                        edge="end"
                        onClick={(e) => handleMoreFileClick(e, libraryFilePayload.path, false)}>
                        <MoreVert />
                    </IconButton>
                }
            >
                <ListItemIcon style={{ paddingLeft: "1em" }}>
                    {fileIcon(libraryFilePayload.mashupMediaType)}
                </ListItemIcon>
                <ListItemText primary={libraryFilePayload.name} />
            </ListItem>
        )
    }

    const fileIcon = (mashupMediaType: MashupMediaType) => {
        switch (mashupMediaType) {
            case MashupMediaType.MUSIC:
                return (
                    <MusicNote />
                )
            case MashupMediaType.PHOTO:
                return (
                    <Image />
                )
            case MashupMediaType.VIDEO:
                return (
                    <Movie />
                )
            default:
                return (
                    <Article />
                )
        }
    }

    return (
        <div
            id="library-files"
            hidden={props.tabPanelPayload.value !== props.tabPanelPayload?.index}>

            <div className="breadcrumb-container">
                <Breadcrumbs className="breadcrumbs">
                    {props.libraryFilesPayload?.breadcrumbPayloads.map((breadcrumbPayload, index) => {
                        return (
                            <Link
                                key={breadcrumbPayload.name}
                                onClick={() => handleClickLibraryFile(breadcrumbPayload.path)}
                                href="#void"
                            >
                                {!index ? 'Root' : breadcrumbPayload.name}
                            </Link>
                        )
                    })}
                </Breadcrumbs>

                <IconButton
                    title="Folder menu"
                    onClick={e => handleMoreFileClick(e, props.folderPath || props.libraryPayload.path, true)}>
                    <MoreVert />
                </IconButton>
            </div>

            <LibraryFileMenu {...props.libraryFolderMenuPayload} />

            <div className="new-line">
                <List>
                    {props.libraryFilesPayload?.libraryFilePayloads?.map(libraryFilePayload => (
                        libraryFilePayload.folder
                            ? renderFolder(libraryFilePayload)
                            : renderFile(libraryFilePayload)
                    ))}
                </List>
            </div>
        </div>
    )
}

export default LibraryFiles