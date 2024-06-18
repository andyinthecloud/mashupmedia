import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material"
import { ChangeEvent, useEffect, useState } from "react"
import { useTranslation } from "react-i18next"
import { useNavigate } from "react-router-dom"
import { CreateAlbumPayload, createAlbum } from "../../../media/music/rest/musicCalls"
import { DialogPageload } from "./DialogPageload"

export type CreateAlbumNameDialogPageload = {
    artistId: number
}


type InternalCreateAlbumNameDialogPageload = {
    dialogPayload: DialogPageload<CreateAlbumPayload>
    errorMessage?: string
}

const CreateAlbumNameDialog = (payload: CreateAlbumNameDialogPageload) => {

    const navigate = useNavigate()
    const { t } = useTranslation('common');

    const [props, setProps] = useState<InternalCreateAlbumNameDialogPageload>({
        dialogPayload: {
            open: false,
            title: 'Create album',
            payload: {
                artistId: payload.artistId || 0,
                name: ''
            }
        }
    })

    useEffect(() => {
        setProps(p => ({
            ...p,
            dialogPayload: {
                ...p.dialogPayload,
                payload: {
                    ...p.dialogPayload.payload,
                    artistId: payload.artistId
                }
            }
        }))

    }, [payload.artistId])

    const handleCancel = () => {
        setProps(p => ({
            ...p,
            errorMessage: '',
            dialogPayload: {
                ...p.dialogPayload,
                open: false
            }
        }))
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
            }
        }))
    }

    const handleClickSave = () => {
        createAlbum(props.dialogPayload.payload).then(response => {
            if (response.ok) {
                setProps(p => ({
                    ...p,
                    dialogPayload: {
                        ...p.dialogPayload,
                        open: false
                    }
                }))
                navigate("/music/album/" + response.parsedBody?.payload.id)
            } else {
                setProps(p => ({
                    ...p,
                    errorMessage: t(response.parsedBody?.errorPayload?.errorCode || '')
                }))
            }
        })
    }

    const handleClickAddAlbum = () => {
        setProps(p => ({
            ...p,
            dialogPayload: {
                ...p.dialogPayload,
                open: true
            }
        }))
    }

    return (
        <div>
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
                        label="Album name"
                        value={props.dialogPayload.payload.name || ''}
                        name="name"
                        onChange={handleInputChange}
                    />

                    {!!props.errorMessage &&
                        <div className="error">{props.errorMessage}</div>
                    }
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
                        onClick={handleClickSave}
                    >Save</Button>
                </DialogActions>
            </Dialog>

            {props.dialogPayload.payload?.artistId > 0 &&
                <div className="new-line" style={{ marginTop: "1em" }}>
                    <Button
                        onClick={handleClickAddAlbum}
                        color="secondary"
                        variant="outlined">Add album</Button>
                </div>
            }
        </div>
    )


}

export default CreateAlbumNameDialog


