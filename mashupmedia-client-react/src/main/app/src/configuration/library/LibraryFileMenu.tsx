import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Menu, MenuItem, TextField } from "@mui/material"
import { Fragment, useEffect, useState } from "react"

export type LibraryFileMenuPayload = {
    anchorElement: HTMLElement | null
    openRenameDialog?: boolean
    openDeleteDialog?: boolean
}

const LibraryFileMenu = (libraryFolderMenuPayload: LibraryFileMenuPayload) => {

    const [props, setProps] = useState<LibraryFileMenuPayload>({
        anchorElement: null
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
            anchorElement: libraryFolderMenuPayload.anchorElement
        }))
    }, [libraryFolderMenuPayload])

    const handleCloseRenameDialog = (): void => {
        setProps(p => ({
            ...p,
            openRenameDialog: false
        }))
    }

    const handleSaveRenameDialog = (): void => {
        setProps(p => ({
            ...p,
            openRenameDialog: false
        }))
    }

    const handleCloseDeleteDialog = (): void => {
        setProps(p => ({
            ...p,
            openDeleteDialog: false
        }))
    }

    return (
        <Fragment>

            <Dialog
                open={props.openRenameDialog || false}
                onClose={handleCloseRenameDialog}
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
                    />
                </DialogContent>
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={handleCloseRenameDialog}
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
                onClose={handleCloseDeleteDialog}
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
                        onClick={handleCloseDeleteDialog}>
                        Cancel
                    </Button>
                    <Button
                        variant="contained"
                        onClick={handleSaveRenameDialog}>
                        OK
                    </Button>
                </DialogActions>
            </Dialog>

            <Menu
                open={isOpen()}
                anchorEl={props.anchorElement}
                onClose={handleCloseMenu}
            >
                <MenuItem
                    onClick={handleRenameClick}>
                    Rename
                </MenuItem>
                <MenuItem
                    onClick={handleDeleteClick}>
                    Delete
                </MenuItem>
            </Menu>
        </Fragment>
    )
}

export default LibraryFileMenu