import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { DialogWithUpdateCallPageload } from "./DialogPageload"


export type EditTextDialogPayload = {
    textFieldLabel: string
    dialogPayload: DialogWithUpdateCallPageload<string>
    multiline?: boolean

}

const EditTextDialog = (payload: EditTextDialogPayload) => {

    const [props, setProps] = useState<EditTextDialogPayload>(payload)


    useEffect(() => {
        setProps(p => ({
            ...p,
            dialogPayload: {
                ...p.dialogPayload,
                open: payload.dialogPayload.open            
            }
        }))

    }, [payload.dialogPayload.open])


    const handleTextChange = (text: string): void => {
        setProps(p => ({
            ...p,
            dialogPayload: {
                ...p.dialogPayload,
                payload: text
            }
        }))
    }

    const handleSave = () => {
        props.dialogPayload.open = false
        props.dialogPayload.updatePayload(props.dialogPayload.payload)
    }

    const handleCancel = () => {
        props.dialogPayload.open = false
        props.dialogPayload.updatePayload('')
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
                    multiline={props.multiline}
                    style={{ marginTop: '0.3em' }}
                    label={props.textFieldLabel}                    
                    value={props.dialogPayload.payload || ''}
                    onChange={e => handleTextChange(e.target.value)}
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

export default EditTextDialog