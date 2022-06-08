import { CheckboxPayload } from "../components/Checkboxes";
import { GroupPayload, RolePayload } from "../settings/ajax/metaCalls";

export const toSelectedRoleValues = (rolePayloadArray: RolePayload[] | undefined): string[] => {
    const selectedValues: string[] = []
    if (rolePayloadArray === undefined || !rolePayloadArray.length) {
        return selectedValues;
    }

    rolePayloadArray.forEach(rolePayload => {
        selectedValues.push(rolePayload.idName)
    })

    return selectedValues
}

export const toSelectedGroupValues = (groupPayloadArray: GroupPayload[] | undefined): string[] => {
    const selectedValues: string[] = []
    if (groupPayloadArray === undefined || !groupPayloadArray.length) {
        return selectedValues;
    }

    groupPayloadArray.forEach(groupPayload => {
        selectedValues.push(String(groupPayload.id))
    })

    return selectedValues
}

export const createRoleCheckboxPayloads = (rolePayloadArray: RolePayload[] | undefined): CheckboxPayload[] => {

    const checkboxPayloads: CheckboxPayload[] = []

    if (rolePayloadArray === undefined || !rolePayloadArray.length) {
        return checkboxPayloads;
    }

    rolePayloadArray.forEach(rolePayload => {
        checkboxPayloads.push({
            name: rolePayload.name,
            value: rolePayload.idName
        })
    });

    return checkboxPayloads
}

export const createGroupCheckboxPayloads = (rolePayloadArray: GroupPayload[] | undefined): CheckboxPayload[] => {

    const checkboxPayloads: CheckboxPayload[] = []

    if (rolePayloadArray === undefined || !rolePayloadArray.length) {
        return checkboxPayloads;
    }

    rolePayloadArray.forEach(groupPayload => {
        checkboxPayloads.push({
            name: groupPayload.name,
            value: '' + groupPayload.id
        })
    });

    return checkboxPayloads
}