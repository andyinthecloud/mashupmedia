import { Checkbox, FormControlLabel } from '@mui/material'
import { ChangeEvent, useEffect, useState } from 'react'
import './Checkboxes.css'

export type CheckboxPayload = {
    name: string
    value: string
    isDisabled?: boolean
}

export type CheckboxHandlerPayload = {
    checkboxPayload: CheckboxPayload,
    isChecked: boolean
    onChange: (value: string, isChecked: boolean) => void
}


export type CheckboxesPayload = {
    isDisabled: boolean
    referenceItems: CheckboxPayload[]
    selectedValues: string[]
    onChange: (value: string[]) => void
}

const Checkboxes = (payload: CheckboxesPayload) => {

    const [props, setProps] = useState<CheckboxesPayload>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])

    const handleChange = (value: string, isChecked: boolean) => {
        const index = props.selectedValues?.indexOf(value)
        processSelectedValues(value, isChecked, index)
        console.log('handleChange: ' + props.selectedValues)      
        props.onChange(props.selectedValues)
    }
    
    const processSelectedValues = (value: string, isChecked: boolean, index: number | undefined): void => {
        if (isChecked) {
            props.selectedValues?.push(value)
        } else {
            if (index !== undefined) {
                props.selectedValues?.splice(index, 1)
            }
        }
        setProps(props)
    }

    return (
        <ul>
            {props.referenceItems.map(function (referenceItem) {
                referenceItem.isDisabled = props.isDisabled                

                const checkboxHandlerPayload: CheckboxHandlerPayload = {
                    checkboxPayload: referenceItem,
                    isChecked: props.selectedValues.some(selectedValue => selectedValue === referenceItem.value),
                    onChange: () => void 0
                }

                return (
                    <CustomCheckbox {...checkboxHandlerPayload} key={referenceItem.value} onChange={handleChange} />
                )
            }
            )}
        </ul>
    )
}

export default Checkboxes


const CustomCheckbox = (payload: CheckboxHandlerPayload) => {

    const [props, setProps] = useState<CheckboxHandlerPayload>({
        ...payload
    })

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setProps({ ...props, isChecked: e.target.checked })
        props.onChange(props.checkboxPayload.value, e.target.checked)
    }

    return (
        <li><FormControlLabel control={<Checkbox
            onChange={handleChange}
            checked={props.isChecked}
        />}
            label={props.checkboxPayload.name}
            disabled={props.checkboxPayload.isDisabled}
        />
        </li>


    )
}