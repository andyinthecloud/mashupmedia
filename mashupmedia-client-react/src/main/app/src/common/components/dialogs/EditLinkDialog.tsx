import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material"
import { ChangeEvent, useEffect, useState } from "react"
import { DialogPageload } from "./DialogPageload"

export type EditLinkDialogPageload = DialogPageload & {
    index: number
    name: string
    link: string
    updateLink: (index: number, text: string, link: string) => void
}

const EditLinkDialog = (payload: EditLinkDialogPageload) => {

    const [props, setProps] = useState<EditLinkDialogPageload>(payload)

    useEffect(() => {
        setProps(p => ({
            ...p,
            open: payload.open
        }))

    }, [payload.open])

    const handleCancel = () => {
        props.updateLink(props.index, '', '')
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>): void => {

        const name = e.target.name
        const text = e.target.value

        setProps(p => ({
            ...p,
            [name]: text
        }))
    }

    const handleSave = () => {
        props.updateLink(props.index, props.name, props.link)
    }

    return (
        <Dialog
            maxWidth="xl"
            fullWidth
            open={props.open}
            onClose={handleCancel}>

            <DialogTitle>{props.title}</DialogTitle>
            <DialogContent>
                <TextField
                    autoFocus
                    required
                    fullWidth
                    style={{ marginTop: '0.3em' }}
                    label="Name"
                    value={props.name || ''}
                    name="name"
                    onChange={handleInputChange}
                />

                <TextField
                    autoFocus
                    required
                    fullWidth
                    style={{ marginTop: '0.3em' }}
                    label="Link"
                    value={props.link || ''}
                    name="link"
                    onChange={handleInputChange}
                />

            </DialogContent>
            <DialogActions>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleCancel}
                >Cancel</Button>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSave}
                >Save</Button>
            </DialogActions>
        </Dialog>
    )
}

export default EditLinkDialog