import { NameValuePayload } from "../../configuration/backend/metaCalls";
import { CheckboxPayload } from "../components/Checkboxes";

export const toSelectedValues = <T,>(checkboxPayloads: CheckboxPayload<T>[] | undefined): T[] => {
    const selectedValues: T[] = []
    if (checkboxPayloads === undefined) {
        return selectedValues
    }

    checkboxPayloads.forEach(checkboxPayload => {
        selectedValues.push(checkboxPayload.value)
    })

    return selectedValues
}

export const toCheckboxPayloads = <T,>(nameValuePayloads: NameValuePayload<T>[] | undefined): CheckboxPayload<T>[] => {

    const checkboxPayloads: CheckboxPayload<T>[] = []

    if (nameValuePayloads === undefined || !nameValuePayloads.length) {
        return checkboxPayloads;
    }

    nameValuePayloads.forEach(nameValuePayload => {
        checkboxPayloads.push({
            name: nameValuePayload.name,
            value: nameValuePayload.value
        })
    });

    return checkboxPayloads
}

export const toNameValuePayloads = <T,>(values: T[]): NameValuePayload<T>[] => {

    const nameValuePayloads: NameValuePayload<T>[] = [];
    values.map(value => {
        nameValuePayloads.push({
            value,
            name: ''
        })
    })
    return nameValuePayloads

}


     
