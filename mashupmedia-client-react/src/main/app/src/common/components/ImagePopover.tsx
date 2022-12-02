import { Popover } from "@mui/material"
import { useEffect, useState } from "react"

export type ImagePopoverPayload = {
    imageSource?: string
    anchorELement?: HTMLElement | null
    timestamp?: number
}

const ImagePopover = (payload: ImagePopoverPayload) => {

    const [props, setProps] = useState<ImagePopoverPayload>()

    useEffect(() => {
        setProps(payload)
    }, [payload])
    

    const open = Boolean(props?.anchorELement);

    const handleClose = () => {
        setProps(p => ({
            ...p,
            anchorELement: null
        }))
    };

    return (
        <Popover
            id="image-popover"
            anchorEl={props?.anchorELement}
            open={open}            
            onClose={handleClose}
            onClick={handleClose}
            anchorOrigin={{
                vertical: 'center',
                horizontal: 'center',
            }}
        >
            <img src={props?.imageSource} />
        </Popover>
    )
}

export default ImagePopover