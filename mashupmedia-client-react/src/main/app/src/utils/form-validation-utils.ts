
export type FieldValidation = {
    name: string
    message: string
}

export type FormValidation = {
    fieldValidations: FieldValidation[]
}

export const isEmpty = (value: string): boolean => !value?.trim().length

export const emptyFieldValidation = (name: string, fieldLabel: string): FieldValidation => ({
    name,
    message: `${fieldLabel} is mandatory, please enter a value.`
})


export const hasFieldError = (name: string, formValidation: FormValidation): boolean => {
    return formValidation?.fieldValidations
        .some(fieldValidation => fieldValidation.name === name)
}

export const fieldValidation = (name: string, formValidation: FormValidation): FieldValidation | undefined => {
    return formValidation?.fieldValidations
        .find(fieldValidation => fieldValidation.name === name)
}

