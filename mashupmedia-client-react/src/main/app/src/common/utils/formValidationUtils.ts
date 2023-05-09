
export type FieldValidation = {
    name: string
    message: string
}

export type FormValidation = {
    fieldValidations: FieldValidation[]
}

export type ServerError = {
    name: string
    field?: string
    defaultMessage: string
    code?: string
}

export type ServerResponsePayload<T> = {
    payload: T
    errorPayload: {
        objectErrors: ServerError[]
        fieldErrors: ServerError[]
    }
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


export const fieldErrorMessage = (name: string, formValidation: FormValidation): string | undefined => {
    return formValidation?.fieldValidations
        .find(fieldValidation => fieldValidation.name === name)?.message
}


export const fieldValidation = (name: string, formValidation: FormValidation): FieldValidation | undefined => {
    return formValidation?.fieldValidations
        .find(fieldValidation => fieldValidation.name === name)
}

export const toFieldValidation = (serverError: ServerError): FieldValidation => ({
    name: serverError.field || serverError.name,
    message: serverError.defaultMessage
})

export const getFirstObjectErrorDefaultMessage = (serverErrors?: ServerError[]): string | null => {
    if (!serverErrors || serverErrors.length === 0) {
        return null
    }

    return serverErrors[0].defaultMessage || null
}
