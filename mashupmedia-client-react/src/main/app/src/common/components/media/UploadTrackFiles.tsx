import { ChangeEvent, useRef, useState, DragEvent } from "react";
import "./UploadTrackFiles.css"
import { Button } from "@mui/material";
import { Audiotrack, CloudUpload } from "@mui/icons-material";
import { t } from "i18next";





export type UploadTrackFilesPayload = {
    selectFiles(fileList: FileList): void
}

const UploadTrackFiles = (payload: UploadTrackFilesPayload) => {
    const uploadFileRef = useRef<HTMLInputElement>(null);


    // window.addEventListener("dragenter",  function(e: DragEvent) {
    //     if (e.currentTarget.id != dropzoneId) {
    //       e.preventDefault();
    //       e.dataTransfer.effectAllowed = "none";
    //       e.dataTransfer.dropEffect = "none";
    //     }
    //   }, false);
      
    //   window.addEventListener("dragover", function(e:DragEvent) {
    //     if (e.currentTarget.id != dropzoneId) {
    //       e.preventDefault();
    //       e.dataTransfer.effectAllowed = "none";
    //       e.dataTransfer.dropEffect = "none";
    //     }
    //   });
      
    //   window.addEventListener("drop", function(e: DragEvent) {
    //     if (e.currentTarget.id != dropzoneId) {
    //       e.preventDefault();
    //       e.dataTransfer.effectAllowed = "none";
    //       e.dataTransfer.dropEffect = "none";
    //     }
    //   });

    const [props] = useState<UploadTrackFilesPayload>(
        payload
    )

    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {
        const fileList = e.target.files
        if (!fileList?.length) {
            return
        }
        props.selectFiles(fileList)
    }

    function handleClickSelectTracks(): void {
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }

    function handleDropFiles(e: DragEvent<HTMLDivElement>): void {
        // e.preventDefault()
        // document.addEventListener('drop', function(e) { e.preventDefault(); }, false);
        console.log(e.dataTransfer.files)
    }

    return (
        <div 
        onDrop={handleDropFiles}
        id="upload-track-files">


            <div className="cloud-icon">
                <CloudUpload
                    color="primary"
                    fontSize="large"
                />
            </div>

            <div>{t("uploadTrackFiles.drag")}</div>

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


        </div>
    )
}

export default UploadTrackFiles