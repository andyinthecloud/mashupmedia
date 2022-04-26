import { Checkbox, FormControlLabel } from '@mui/material'
import './LineItems.css'

export type LineItemPayload = {
    id: string
    name: string
    isDisabled?: boolean
}


export type LineItemPayloadArray = {
    lineItemPayloads: LineItemPayload[]
}

const LineItems = (props: LineItemPayloadArray) => {

    return (
        <ul>
            {props.lineItemPayloads.map(function (lineItemPayload) {
                lineItemPayload.isDisabled = true

                return (
                    <LineItem key={lineItemPayload.id} {...lineItemPayload} ></LineItem>
                )
            }
            )}
        </ul>
    )
}

export default LineItems


const LineItem = (props: LineItemPayload) => {
    return (
        <li><FormControlLabel control={<Checkbox />} label={props.name}  disabled={props.isDisabled} /></li>
    )
}