import { ChangeEvent, useEffect, useRef, useState } from "react"
import { MetaImagePayload } from "../../../media/music/rest/musicUploadCalls"
import { MenuMetaPayload } from "../../../media/rest/mediaCalls"
import ImagePopover, { ImagePopoverPayload } from "../ImagePopover"
import ImageMenu from "../menus/ImageMenu"
import './ManageMetaImages.css'
import { ArrayMoveType, updateMetaPayloadInArray } from "./metaUtils"

export type ManageMetaImagesPayload = {
    metaImagePayloads: MetaImagePayload[]
    updateMetaImages(metaImagePayloads: MetaImagePayload[]): void
    uploadFiles(files: FileList): void
    getImageUrl(id: number): string
    editor: boolean
    triggerUploadImage?: number
}

type InternalManageMetaImagesPayload = {
    metaImageMenuPayload: MenuMetaPayload<MetaImagePayload>
    manageMetaImagesPayload: ManageMetaImagesPayload
    imagePopover: ImagePopoverPayload
}

const ManageMetaImages = (payload: ManageMetaImagesPayload) => {

    const uploadFileRef = useRef<HTMLInputElement>(null);
    const metaImagePayloadsRef = useRef<MetaImagePayload[]>([])

    const [props, setProps] = useState<InternalManageMetaImagesPayload>({
        metaImageMenuPayload: {
            anchorElement: null,
            open: false,
            edit: handleEdit,
            delete: handleDelete,
            moveTop: handleMoveTop,
            moveUpOne: handleMoveUpOne,
            moveDownOne: handleMoveDownOne,
            moveBottom: handleMoveBottom
        },
        manageMetaImagesPayload: payload,
        imagePopover: {
            source: '',
            trigger: 0
        }
    })

    function handleEdit(metaImagePayload: MetaImagePayload): void {
        setProps(p => ({
            ...p,
            metaImageMenuPayload: {
                ...p.metaImageMenuPayload,
                open: false
            },
            imagePopover: {
                source: p.manageMetaImagesPayload.getImageUrl(metaImagePayload.id),
                trigger: Date.now()
            }
        }))
    }

    function updateMetaImages(): void {
        setProps(p => ({
            ...p,
            metaImageMenuPayload: {
                ...p.metaImageMenuPayload,
                open: false
            },
            manageMetaImagesPayload: {
                ...p.manageMetaImagesPayload,
                metaImagePayloads: metaImagePayloadsRef.current
            }
        }))


        metaImagePayloadsRef.current.forEach((metaImage, index) => {
            metaImage.rank = index
        })

        props.manageMetaImagesPayload?.updateMetaImages(metaImagePayloadsRef.current)

    }

    function handleDelete(metaImagePayload: MetaImagePayload): void {
        updateMetaPayloadInArray(metaImagePayloadsRef.current, metaImagePayload, ArrayMoveType.DELETE)
        updateMetaImages()
    }


    function handleMoveTop(metaImagePayload: MetaImagePayload): void {
        updateMetaPayloadInArray(metaImagePayloadsRef.current, metaImagePayload, ArrayMoveType.TOP)
        updateMetaImages()
    }

    function handleMoveUpOne(metaImagePayload: MetaImagePayload): void {
        updateMetaPayloadInArray(metaImagePayloadsRef.current, metaImagePayload, ArrayMoveType.UP)
        updateMetaImages()
    }


    function handleMoveDownOne(metaImagePayload: MetaImagePayload): void {
        updateMetaPayloadInArray(metaImagePayloadsRef.current, metaImagePayload, ArrayMoveType.DOWN)
        updateMetaImages()
    }

    function handleMoveBottom(metaImagePayload: MetaImagePayload): void {
        updateMetaPayloadInArray(metaImagePayloadsRef.current, metaImagePayload, ArrayMoveType.BOTTOM)
        updateMetaImages()
    }

    useEffect(() => {
        metaImagePayloadsRef.current.push(...payload.metaImagePayloads)

        setProps(p => ({
            ...p,
            manageMetaImagesPayload: {
                ...p.manageMetaImagesPayload,
                metaImagePayloads: payload.metaImagePayloads
            }
        }))
    }, [payload.metaImagePayloads])



    useEffect(() => {
        if (payload.triggerUploadImage) {
            addImage()
        }

    }, [payload.triggerUploadImage])

    function addImage(): void {
        
        if (uploadFileRef) {
            uploadFileRef.current?.click()
        }
    }


    const handleChangeFolder = (e: ChangeEvent<HTMLInputElement>): void => {

        const files = e.target.files
        if (!files?.length) {
            return
        }

        props.manageMetaImagesPayload.uploadFiles(files)

    }

    const handleClickImage = (anchorElement: HTMLElement, payload: MetaImagePayload): void => {
        if (props.manageMetaImagesPayload.editor) {
            setProps(p => ({
                ...p,
                metaImageMenuPayload: {
                    ...p.metaImageMenuPayload,
                    anchorElement,
                    open: true,
                    payload
                }
            }))
        } else {
            handleEdit(payload)
        }
    }

    return (
        <div className="manage-meta-images">
            <ImagePopover {...props.imagePopover} />
            <ImageMenu {...props.metaImageMenuPayload} />

            <div className="images">
                {props.manageMetaImagesPayload.metaImagePayloads?.map(payload => {
                    return (
                        <div key={payload.id} className="item">
                            <img
                                src={props.manageMetaImagesPayload.getImageUrl(payload.id)}
                                onClick={e => handleClickImage(e.currentTarget, payload)} />
                        </div>
                    )
                })}
            </div>

            <input
                style={{ display: 'none' }}
                type="file"
                multiple
                accept="image/png, image/jpeg"
                ref={uploadFileRef}
                onChange={e => handleChangeFolder(e)}
            />

            {/* <Button
                className="edit-content"
                variant="outlined"
                endIcon={<AddAPhoto />}
                color="secondary"
                onClick={handleUploadImagesClick}
            >
                {t('label.image')}
            </Button> */}

        </div>
    )
}

export default ManageMetaImages