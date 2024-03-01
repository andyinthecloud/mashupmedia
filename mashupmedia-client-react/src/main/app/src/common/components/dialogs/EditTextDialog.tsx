import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material"
import { useEffect, useState } from "react"
import { DialogPageload } from "./DialogPageload"


export type EditTextDialogPageload = DialogPageload & {
    textFieldLabel: string
    text: string
    multiline?: boolean
    updateText: (text: string) => void
}

const EditTextDialog = (payload: EditTextDialogPageload) => {

    const [props, setProps] = useState<EditTextDialogPageload>(payload)


    useEffect(() => {
        setProps(p => ({
            ...p,
            open: payload.open
        }))

    }, [payload.open])


    const handleTextChange = (text: string): void => {
        setProps(p => ({
            ...p,
            text
        }))
    }

    const handleSave = () => {
        props.updateText(props.text)
    }

    const handleCancel = () => {
        props.updateText('')
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
                    multiline={props.multiline}
                    style={{ marginTop: '0.3em' }}
                    label={props.textFieldLabel}                    
                    value={props.text || ''}
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