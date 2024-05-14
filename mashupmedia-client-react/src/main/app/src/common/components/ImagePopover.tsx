import { useEffect, useState } from "react"
import './ImagePopover.css'


export type ImagePopoverPayload = {
    source: string
    trigger: number
}

const ImagePopover = (payload: ImagePopoverPayload) => {

    const [props, setProps] = useState<ImagePopoverPayload>(payload)

    useEffect(() => {
        setProps(payload)
    }, [payload.trigger])


    const handleClose = () => {
        setProps(p => ({
            ...p,
            source: ''
        }))
    }

    return (
        <div
            id="image-popover"
            onClick={handleClose}
            style={{
                display: props.source ? 'block' : 'none',
                backgroundImage: props.source ? `url(${props.source})` : ''
            }}
        />
    )
}

export default ImagePopover