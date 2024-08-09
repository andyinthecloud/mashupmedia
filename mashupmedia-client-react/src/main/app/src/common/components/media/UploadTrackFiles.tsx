import { Audiotrack, CloudUpload, RemoveCircleOutline } from "@mui/icons-material";
import { Button, IconButton } from "@mui/material";
import { t } from "i18next";
import { ChangeEvent, DragEvent, useRef, useState } from "react";
import "./UploadTrackFiles.css";


export enum FileType {
    AUDIO
}

export type UploadTrackFilesPayload = {
    selectFiles(files: File[]): void
    fileType: FileType
}

type InternalUploadTrackFilesPayload = {
    uploadTrackFilesPayload: UploadTrackFilesPayload
    files: File[]
    dragging: boolean
}

const UploadTrackFiles = (payload: UploadTrackFilesPayload) => {
    const uploadFileRef = useRef<HTMLInputElement>(null);
    const filesRef = useRef<File[]>([])

    const [props, setProps] = useState<InternalUploadTrackFilesPayload>({
        files: [],
        uploadTrackFilesPayload: payload,
        dragging: false
    })

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {
        const selectedFiles = e.target.files
        if (!selectedFiles?.length) {
            return
        }

        const files = props.files.concat(Array.from(selectedFiles))

        setProps(p => ({
            ...p,
            files
        }))
        filesRef.current = files

        props.uploadTrackFilesPayload.selectFiles(filesRef.current)
    }

    function handleClickSelectTracks(): void {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    function handleDropFiles(e: DragEvent<HTMLDivElement>): void {
        e.preventDefault()

        const fileTypeValue = getFileTypeInLowerCase()
        const selectedFiles = Array.from(e.dataTransfer.files)
            .filter(file => file.type.toLowerCase().startsWith(fileTypeValue))
        const files = props.files.concat(selectedFiles)
        setProps(p => ({
            ...p,
            files,
            dragging: false
        }))
        filesRef.current = files

        props.uploadTrackFilesPayload.selectFiles(filesRef.current)

    }

    function getFileTypeInLowerCase(): string {
        return FileType[props.uploadTrackFilesPayload.fileType].toLowerCase()
    }


    function handleDragOver(e: DragEvent<HTMLDivElement>): void {
        e.preventDefault()
        handleDragHighlight(true)
    }

    function handleDragHighlight(dragging: boolean): void {
        setProps(p => ({
            ...p,
            dragging
        }))
    }

    function handleClickRemoveFile(index: number): void {
        filesRef.current.splice(index, 1)
        setProps(p => ({
            ...p,
            files: filesRef.current
        }))

        props.uploadTrackFilesPayload.selectFiles(filesRef.current)
    }


    return (
        <div
            onDrop={handleDropFiles}
            onDragOver={handleDragOver}
            onDragExit={() => handleDragHighlight(false)}
            id="upload-track-files"
            className={props.dragging ? "highlight" : ""}
        >


            <div className="cloud-icon">
                <CloudUpload
                    color="primary"
                    fontSize="large"
                />
            </div>

            <div>{t("uploadTrackFiles.drag", { fileType: getFileTypeInLowerCase() })}</div>

            <div>{t("uploadTrackFiles.or")} </div>


            <input
                style={{ display: 'none' }}
                type="file"
                multiple
                accept="audio/*"
                ref={uploadFileRef}
                onChange={e => handleChangeFolder(e)}
            />

            <Button
                className="edit-content"
                variant="outlined"
                endIcon={<Audiotrack />}
                color="primary"
                onClick={handleClickSelectTracks}
            >
                {t('uploadTrackFiles.browse')}
            </Button>


            <div className="files">
                {props.files.map((file, index) => (
                    <div key={index} className="file">
                        <div>{file.name}</div>
                        <div>
                            <IconButton 
                            color="secondary"
                            onClick={() => handleClickRemoveFile(index)}>
                                <RemoveCircleOutline />
                            </IconButton>
                        </div>
                    </div>



                ))}
            </div>

        </div>
    )
}

export default UploadTrackFiles