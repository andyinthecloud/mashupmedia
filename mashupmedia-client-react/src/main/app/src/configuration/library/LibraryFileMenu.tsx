import { Delete, Edit, Folder, UploadFile } from "@mui/icons-material"
import { Button, Dialog, DialogActions, DialogContent, DialogContentText, Menu, MenuItem, TextField } from "@mui/material"
import { ChangeEvent, Fragment, useEffect, useRef, useState } from "react"
import { useDispatch } from "react-redux"
import { NotificationType, addNotification } from "../../common/notification/notificationSlice"
import { renameFile } from "../backend/libraryFileCalls"
import './LibraryFileMenu.css'

export type LibraryFileMenuPayload = {
    anchorElement: HTMLElement | null
    libraryId?: number
    path?: string
    openRenameDialog?: boolean
    openDeleteDialog?: boolean
    enableUpload: boolean,
    renameValue?: string
}

const LibraryFileMenu = (libraryFolderMenuPayload: LibraryFileMenuPayload) => {


    const uploadFolderRef = useRef<HTMLInputElement>(null);
    const uploadFileRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (uploadFolderRef.current !== null) {
            uploadFolderRef.current.setAttribute("directory", "");
            uploadFolderRef.current.setAttribute("webkitdirectory", "");
        }
    }, [uploadFolderRef]);


    const [props, setProps] = useState<LibraryFileMenuPayload>({
        anchorElement: null,
        enableUpload: false
    })

    const isOpen = (): boolean => {
        return Boolean(props.anchorElement)
    }

    const handleRenameClick = (): void => {
        setProps(p => ({
            ...p,
            anchorElement: null,
            openRenameDialog: true
        }))
    }

    const handleDeleteClick = (): void => {
        setProps(p => ({
            ...p,
            anchorElement: null,
            openDeleteDialog: true
        }))
    }

    const handleCloseMenu = (): void => {
        setProps(p => ({
            ...p,
            anchorElement: null
        }))
    }

    useEffect(() => {
        setProps(p => ({
            ...p,
            anchorElement: libraryFolderMenuPayload.anchorElement,
            enableUpload: libraryFolderMenuPayload.enableUpload,
            libraryId: libraryFolderMenuPayload.libraryId,
            path: libraryFolderMenuPayload.path
        }))
    }, [libraryFolderMenuPayload])

    const dispatch = useDispatch()

    const handleSaveRenameDialog = (): void => {

        setProps(p => ({
            ...p,
            renameValue: '',
            openRenameDialog: false
        }))

        renameFile({
            libraryId: props.libraryId || 0,
            name: props.renameValue || '',
            path: props.path || ''
        }).then(response => {
            if (response.ok) {
                dispatch(
                    addNotification({
                        message: 'File renamed.',
                        notificationType: NotificationType.SUCCESS
                    })
                )
            } else {
                dispatch(
                    addNotification({
                        message: 'Unable to rename file.',
                        notificationType: NotificationType.ERROR
                    })
                )
            }
        })


    }

    const handleCloseDialog = (): void => {
        setProps(p => ({
            ...p,
            renameValue: '',
            openDeleteDialog: false,
            openRenameDialog: false
        }))
    }


    const handleUploadFolderClick = (): void => {
        if (uploadFolderRef) {
            uploadFolderRef.current?.click()
        }
    }

    const handleUploadFilesClick = (): void => {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {
        setProps(p => ({
            ...p,
            fileList: e.target.files || undefined,
            uploadDisabled: false
        }))
    }

    const handleRenameFileChange = (e: ChangeEvent<HTMLInputElement>): void => {
        setProps(p => ({
            ...p,
            renameValue: e.target.value
        })
        )
    }

    return (
        <Fragment>

            <Dialog
                open={props.openRenameDialog || false}
                onClose={handleCloseDialog}
            >
                <DialogContent>

                    <TextField
                        autoFocus
                        required
                        margin="dense"
                        name="rename"
                        label="Rename file"
                        type="email"
                        fullWidth
                        variant="standard"
                        value={props.renameValue}
                        onChange={handleRenameFileChange}
                    />
                </DialogContent>
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={handleCloseDialog}
                        color="secondary">
                        Cancel
                    </Button>
                    <Button
                        variant="contained"
                        onClick={handleSaveRenameDialog}
                    >
                        Save
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog
                open={props.openDeleteDialog || false}
                onClose={handleCloseDialog}
            >
                <DialogContent>
                    <DialogContentText>
                        Please confirm to delete the folder and all it&apos;s contents.
                    </DialogContentText>

                </DialogContent>
                <DialogActions>
                    <Button
                        variant="contained"
                        color="secondary"
                        onClick={handleCloseDialog}>
                        Cancel
                    </Button>
                    <Button
                        variant="contained"
                        onClick={handleSaveRenameDialog}>
                        OK
                    </Button>
                </DialogActions>
            </Dialog>


            <input
                style={{ display: 'none' }}
                type="file"
                id="raised-button-file"
                multiple
                ref={uploadFileRef}
                onChange={e => handleChangeFolder(e)}
            />

            <input
                style={{ display: 'none' }}
                type="file"
                id="raised-button-folder"
                ref={uploadFolderRef}
                onChange={e => handleChangeFolder(e)}
            />

            <Menu
                open={isOpen()}
                anchorEl={props.anchorElement}
                onClose={handleCloseMenu}
                id="library-file-menu"
            >
                <MenuItem
                    onClick={handleRenameClick}>
                    <Edit />
                    Rename
                </MenuItem>
                <MenuItem
                    onClick={handleDeleteClick}>
                    <Delete />
                    Delete
                </MenuItem>
                {props.enableUpload &&
                    <MenuItem
                        onClick={handleUploadFilesClick}>
                        <UploadFile />
                        Upload files
                    </MenuItem>
                }
                {props.enableUpload &&
                    <MenuItem
                        onClick={handleUploadFolderClick}>
                        <Folder />
                        Upload folder
                    </MenuItem>
                }
            </Menu>
        </Fragment>
    )
}

export default LibraryFileMenu