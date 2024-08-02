import { MoreVert, OpenInNew } from "@mui/icons-material"
import { IconButton } from "@mui/material"
import { useEffect, useRef, useState } from "react"
import { ExternalLinkPayload, MenuMetaPayload } from "../../../media/rest/mediaCalls"
import EditLinkDialog, { EditLinkDialogPageload } from "../dialogs/EditLinkDialog"
import LinkMenu from "../menus/LinkMenu"
import './ManageExternalLinks.css'

export type ManageExternalLinksPayload = {
    externalLinkPayloads: ExternalLinkPayload[]
    updateExternalLinks(externalLinkPayloads: ExternalLinkPayload[]): void
    triggerAddExternalLink?: number
}


type InternalManageExternalLinksPayload = {
    metaExternalLinkMenuPayload: MenuMetaPayload<ExternalLinkPayload>
    manageExternalLinksPayload: ManageExternalLinksPayload,
    editLinkDialogPageload: EditLinkDialogPageload
}


const ManageExternalLinks = (payload: ManageExternalLinksPayload) => {

    const externalLinkPayloadsRef = useRef<ExternalLinkPayload[]>([])

    const [props, setProps] = useState<InternalManageExternalLinksPayload>({
        metaExternalLinkMenuPayload: {
            anchorElement: null,
            open: false,
            edit: handleEdit,
            delete: handleDelete,
            moveTop: handleMoveTop,
            moveUpOne: handleMoveUpOne,
            moveDownOne: handleMoveDownOne,
            moveBottom: handleMoveBottom
        },
        manageExternalLinksPayload: payload,
        editLinkDialogPageload: {
            dialogPayload: {
                open: false,
                title: 'Edit external link',
                payload: {
                    id: 0,
                    link: '',
                    name: '',
                    rank: 0
                },
                updatePayload: updateExternalLink
            }
        }
    })

    useEffect(() => {
        externalLinkPayloadsRef.current.push(...payload.externalLinkPayloads)

        setProps(p => ({
            ...p,
            manageExternalLinksPayload: {
                ...p.manageExternalLinksPayload,
                externalLinkPayloads: payload.externalLinkPayloads

            }
        }))
    }, [payload.externalLinkPayloads])


    useEffect(() => {
        if (payload.triggerAddExternalLink) {
            openNewExternalLinkDialog()
        }

    }, [payload.triggerAddExternalLink])

    function updateExternalLink(externalLinkPayload: ExternalLinkPayload): void {
        addExternalLinkPayload(externalLinkPayload)
        updateExternalLinks()
    }

    function addExternalLinkPayload(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload.name && !externalLinkPayload.link) {
            return
        }

        const index = externalLinkPayloadsRef.current.findIndex(item => item.rank == externalLinkPayload.rank)
        if (index < 0) {
            externalLinkPayloadsRef.current.push(externalLinkPayload)
            return
        }

        externalLinkPayloadsRef.current[index] = externalLinkPayload
    }

    function handleEdit(externalLinkPayload: ExternalLinkPayload): void {
        setProps(p => ({
            ...p,
            metaExternalLinkMenuPayload: {
                ...p.metaExternalLinkMenuPayload,
                open: false
            },
            editLinkDialogPageload: {
                ...p.editLinkDialogPageload,
                dialogPayload: {
                    ...p.editLinkDialogPageload.dialogPayload,
                    open: true,
                    payload: externalLinkPayload
                }
            }
        }))
    }

    function handleDelete(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload || !externalLinkPayloadsRef.current?.length) {
            return
        }

        externalLinkPayloadsRef.current.splice(externalLinkPayload.rank, 1)
        updateExternalLinks()
    }

    function handleMoveTop(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload || !externalLinkPayloadsRef.current?.length) {
            return
        }

        externalLinkPayloadsRef.current.splice(externalLinkPayload.rank, 1)
        externalLinkPayload.rank = 0
        externalLinkPayloadsRef.current.splice(0, 0, externalLinkPayload)
        updateExternalLinks()
    }

    function handleMoveUpOne(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload || !externalLinkPayloadsRef.current?.length) {
            return
        }

        externalLinkPayloadsRef.current.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank - 1
        externalLinkPayload.rank = rank
        externalLinkPayloadsRef.current.splice(rank, 0, externalLinkPayload)
        updateExternalLinks()
    }

    function handleMoveDownOne(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload || !externalLinkPayloadsRef.current?.length) {
            return
        }

        externalLinkPayloadsRef.current.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayload.rank + 1
        externalLinkPayload.rank = rank
        externalLinkPayloadsRef.current.splice(rank, 0, externalLinkPayload)
        updateExternalLinks()
    }

    function handleMoveBottom(externalLinkPayload: ExternalLinkPayload): void {
        if (!externalLinkPayload || !externalLinkPayloadsRef.current?.length) {
            return
        }

        externalLinkPayloadsRef.current.splice(externalLinkPayload.rank, 1)
        const rank = externalLinkPayloadsRef.current.length - 1
        externalLinkPayload.rank = rank
        externalLinkPayloadsRef.current.splice(rank + 1, 0, externalLinkPayload)
        updateExternalLinks()
    }

    function updateExternalLinks() {
        setProps(p => ({
            ...p,
            editLinkDialogPageload: {
                ...p.editLinkDialogPageload,
                dialogPayload: {
                    ...p.editLinkDialogPageload.dialogPayload,
                    open: false
                }
            },
            metaExternalLinkMenuPayload: {
                ...p.metaExternalLinkMenuPayload,
                open: false
            }
        }))

        externalLinkPayloadsRef.current.forEach((externalLink, index) => {
            externalLink.rank = index
        })

        props.manageExternalLinksPayload.updateExternalLinks(externalLinkPayloadsRef.current)
    }

    function openNewExternalLinkDialog(): void {
        const rank = externalLinkPayloadsRef.current.length || 0
        setProps(p => ({
            ...p,
            editLinkDialogPageload: {
                ...p.editLinkDialogPageload,
                dialogPayload: {
                    ...p.editLinkDialogPageload.dialogPayload,
                    open: true,
                    payload: {
                        id: 0,
                        name: '',
                        link: '',
                        rank: rank
                    }
                }
            }
        }))

    }

    function handleClickLinkMenuIcon(anchorElement: HTMLElement, payload: ExternalLinkPayload): void {
        setProps(p => ({
            ...p,
            metaExternalLinkMenuPayload: {
                ...p.metaExternalLinkMenuPayload,
                anchorElement,
                open: true,
                payload
            }
        }))
    }

    return (
        <div className="manage-external-links">
            <EditLinkDialog {...props.editLinkDialogPageload} />
            <LinkMenu {...props.metaExternalLinkMenuPayload} />

            <div className="links">

                {externalLinkPayloadsRef.current.map((externalLinkPayload: ExternalLinkPayload) => {
                    return (
                        <div className="item" key={externalLinkPayload.id}>

                            <a target="_blank" rel="noreferrer" href={externalLinkPayload.link}>
                                <OpenInNew
                                    className="icon"
                                    color="secondary" />
                                <span>{externalLinkPayload.name}</span>
                            </a>

                            <IconButton
                                onClick={(e) => handleClickLinkMenuIcon(e.currentTarget, externalLinkPayload)}
                                color="secondary">
                                <MoreVert />
                            </IconButton>

                        </div>
                    )
                })}
            </div>

        </div>
    )
}

export default ManageExternalLinks