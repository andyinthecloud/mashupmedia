import { Checkbox, FormControlLabel } from '@mui/material'
import { ChangeEvent, useEffect, useState } from 'react'
import './Checkboxes.css'

export type CheckboxPayload<T> = {
    name: string
    value: T
    isDisabled?: boolean
}

export type CheckboxHandlerPayload<T> = {
    checkboxPayload: CheckboxPayload<T>
    isChecked: boolean
    onChange: (value: T, isChecked: boolean) => void
    isDisabled?: boolean
}


export type CheckboxesPayload<T> = {
    isDisabled?: boolean
    referenceItems: CheckboxPayload<T>[]
    selectedValues: T[]
    error?: boolean
    helperText?: string
    onChange: (value: T[]) => void
}

const Checkboxes = <T,>(payload: CheckboxesPayload<T>) => {

    const [props, setProps] = useState<CheckboxesPayload<T>>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])

    const handleChange = (value: T, isChecked: boolean) => {
        const index = props.selectedValues?.indexOf(value)
        processSelectedValues(value, isChecked, index)
        props.onChange(props.selectedValues)
    }

    const processSelectedValues = (value: T, isChecked: boolean, index: number | undefined): void => {
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
                referenceItem.isDisabled = props.isDisabled || false
                const checkboxHandlerPayload: CheckboxHandlerPayload<T> = {
                    checkboxPayload: referenceItem,
                    isChecked: props.selectedValues.some(selectedValue => referenceItem.value === selectedValue),
                    onChange: () => void 0,
                    isDisabled: props.isDisabled
                }

                return (
                    <CustomCheckbox {...checkboxHandlerPayload} key={String(referenceItem.value)} onChange={handleChange} />
                )
            }
            )}

            {props.error &&
                <li className='error'>{props.helperText}</li>
            }
        </ul>


    )
}

export default Checkboxes


const CustomCheckbox = <T,>(payload: CheckboxHandlerPayload<T>) => {

    const [props, setProps] = useState<CheckboxHandlerPayload<T>>({
        ...payload
    })

    useEffect(() => {
        setProps(payload)
    }, [payload])

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setProps({ ...props, isChecked: e.target.checked })
        props.onChange(props.checkboxPayload.value, e.target.checked)
    }

    return (
        <li><FormControlLabel control={<Checkbox
            onChange={handleChange}
            checked={props.isChecked}
            disabled={props.isDisabled}
        />}
            label={props.checkboxPayload.name}
            disabled={props.checkboxPayload.isDisabled}
        />
        </li>


    )
}