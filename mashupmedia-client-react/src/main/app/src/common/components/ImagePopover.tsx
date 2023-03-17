import { Popover } from "@mui/material"
import { useEffect, useState } from "react"
import './ImagePopover.css'


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
                vertical: 'top',
                horizontal: 'left',
            }}
            transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
            }}
        
        >
            <img src={props?.imageSource} />
        </Popover>
    )
}

export default ImagePopover