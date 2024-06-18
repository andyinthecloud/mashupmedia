import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material"
import { ChangeEvent, useEffect, useState } from "react"
import { ExternalLinkPayload } from "../../../media/rest/mediaCalls"
import { DialogWithUpdateCallPageload } from "./DialogPageload"

export type EditLinkDialogPageload = {
    dialogPayload: DialogWithUpdateCallPageload<ExternalLinkPayload>
}

const EditLinkDialog = (payload: EditLinkDialogPageload) => {

    const [props, setProps] = useState<EditLinkDialogPageload>(payload)

    useEffect(() => {
        console.log('editlinkdialog', payload.dialogPayload.open)

        setProps(p => ({
            ...p,
            dialogPayload: payload.dialogPayload
            // dialogPayload: {
            //     ...p.dialogPayload,
            //     open: payload.dialogPayload.open
            // }
        }))

    }, [payload.dialogPayload])

    const handleCancel = () => {
        props.dialogPayload.open = false
        props.dialogPayload.updatePayload({
            id: 0,
            link: '',
            name: '',
            rank: -1
        })
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement>): void => {

        const name = e.target.name
        const text = e.target.value

        setProps(p => ({
            ...p,
            dialogPayload: {
                ...p.dialogPayload,
                payload: {
                    ...p.dialogPayload.payload,
                    [name]: text
                }
            },

        }))
    }

    const handleSave = () => {
        props.dialogPayload.updatePayload(props.dialogPayload.payload)
    }

    return (
        <Dialog
            maxWidth="xl"
            fullWidth
            open={props.dialogPayload.open}
            onClose={handleCancel}>

            <DialogTitle>{props.dialogPayload.title}</DialogTitle>
            <DialogContent>
                <TextField
                    autoFocus
                    required
                    fullWidth
                    style={{ marginTop: '0.3em' }}
                    label="Name"
                    value={props.dialogPayload.payload.name || ''}
                    name="name"
                    onChange={handleInputChange}
                />

                <TextField
                    autoFocus
                    required
                    fullWidth
                    style={{ marginTop: '0.3em' }}
                    label="Link"
                    value={props.dialogPayload.payload.link || ''}
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