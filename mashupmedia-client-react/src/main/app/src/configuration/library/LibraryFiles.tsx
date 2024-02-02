import React, { ChangeEvent, FormEvent, useEffect, useRef, useState } from "react"
import { LibraryPayload, LibraryTypePayload } from "../backend/libraryCalls"
import { LibraryPagePayload, TabPanelPayload } from "./Library"
import { Button, IconButton, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material"
import { Folder, MoreVert, UploadFile } from "@mui/icons-material"
import { LibraryFilePayload, getLibraryFiles } from "../backend/libraryFileCalls"
import { useSelector } from "react-redux"
import { RootState } from "../../common/redux/store"
import LibraryFileMenu, { LibraryFileMenuPayload } from "./LibraryFileMenu"


type LibraryFilesPayload = {
    libraryPayload: LibraryPayload
    tabPanelPayload: TabPanelPayload
    uploadFileList?: FileList
    libraryFilePayloads?: LibraryFilePayload[]
    uploadDisabled: boolean
    folderPath?: string
    libraryFolderMenuPayload: LibraryFileMenuPayload

}

const LibraryFiles = (libraryPagePayload: LibraryPagePayload) => {

    const tabIndex = 1;

    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const uploadFolderRef = useRef<HTMLInputElement>(null);
    const uploadFileRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (uploadFolderRef.current !== null) {
            uploadFolderRef.current.setAttribute("directory", "");
            uploadFolderRef.current.setAttribute("webkitdirectory", "");
        }
    }, [uploadFolderRef]);


    const [props, setProps] = useState<LibraryFilesPayload>({
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
            anchorElement: null
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
                    libraryFilePayloads: response.parsedBody,
                    libraryPayload: libraryPagePayload.libraryPayload,
                    tabPanelPayload: {
                        index: tabIndex,
                        value: libraryPagePayload.tabPanelPayload.value
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
                    libraryFilePayloads: response.parsedBody
                }))
            }
        })
    }

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {
        console.log('handleChangeFolder')

        setProps(p => ({
            ...p,
            fileList: e.target.files || undefined,
            uploadDisabled: false
        }))
    }


    const handleClickUploadFolder = (): void => {
        if (props.uploadFileList == null) {
            return
        }
        Array.from(props.uploadFileList).forEach(file => {
            console.log('selected file:', file)
        })
    }

    const handleIconFolderClick = (): void => {
        if (uploadFolderRef) {
            uploadFolderRef.current?.click()
        }
    }

    const handleIconFileClick = (): void => {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    const handleMoreFileClick = (event: React.MouseEvent<HTMLElement>): void => {
        setProps(p => ({
            ...p,
            libraryFolderMenuPayload: {
                anchorElement: event.currentTarget
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
                    onClick={handleMoreFileClick}>
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
                    <IconButton edge="end">
                        <MoreVert />
                    </IconButton>
                }
            >
                <ListItemIcon>
                    <Folder />
                </ListItemIcon>
                <ListItemText primary={libraryFilePayload.name} />
            </ListItem>
        )
    }

    return (
        <div
            hidden={props.tabPanelPayload.value !== props.tabPanelPayload?.index}>
            <div className="float-right">

                <Button
                    variant="contained"
                    color="primary"
                    type="button"
                    onClick={handleClickUploadFolder}
                    disabled={props.uploadDisabled}
                >
                    Upload
                </Button>

            </div>

            <div className="new-line">
                <input
                    style={{ display: 'none' }}
                    type="file"
                    id="raised-button-folder"
                    ref={uploadFolderRef}
                    onChange={e => handleChangeFolder(e)}
                />

                <IconButton
                    title="Upload folder"
                    onClick={handleIconFolderClick}>
                    <Folder />
                </IconButton>

                <input
                    style={{ display: 'none' }}
                    type="file"
                    id="raised-button-file"
                    multiple
                    ref={uploadFileRef}
                    onChange={e => handleChangeFolder(e)}
                />

                <IconButton
                    title="Upload file"
                    onClick={handleIconFileClick}>
                    <UploadFile />
                </IconButton>

            </div>

            <LibraryFileMenu {...props.libraryFolderMenuPayload} />

            <div className="new-line">
                <List>
                    {props.libraryFilePayloads?.map(libraryFilePayload => (
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