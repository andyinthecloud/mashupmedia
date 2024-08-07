import { t } from "i18next"

export type FieldValidation = {
    name: string
    messageCode: string
}

export type FormValidationPayload<T> = {
    payload: T
    formValidation: FormValidation
}

export type FormValidation = {
    fieldValidations: FieldValidation[]
}

export type ServerError = {
    objectName: string
    field?: string
    defaultMessage: string
    code?: string[]
}


export type ErrorPayload = {
    errorCode?: string
    objectErrors: ServerError[]
    fieldErrors: ServerError[]

}

export type ServerResponsePayload<T> = {
    payload: T
    errorPayload: ErrorPayload    
}

export const isEmpty = (value?: string): boolean => !value?.trim().length

export const emptyFieldValidation = (name: string, fieldLabel: string): FieldValidation => ({
    name,
    messageCode: `${fieldLabel} is mandatory, please enter a value.`
})


export const hasFieldError = (name: string, formValidation: FormValidation): boolean => {
    return formValidation?.fieldValidations
        .some(fieldValidation => fieldValidation.name === name)
}

export const translateFieldErrorMessage = (name: string, formValidation: FormValidation): string | undefined => {
    const message = fieldErrorMessage(name, formValidation)
    return message ? t(message) : message
}


export const fieldErrorMessage = (name: string, formValidation: FormValidation): string | undefined => {
    return formValidation?.fieldValidations
        .find(fieldValidation => fieldValidation.name === name)?.messageCode
}




export const fieldValidation = (name: string, formValidation: FormValidation): FieldValidation | undefined => {
    return formValidation?.fieldValidations
        .find(fieldValidation => fieldValidation.name === name)
}

export const toFieldValidation = (serverError: ServerError): FieldValidation => ({
    name: serverError.field || serverError.objectName,
    messageCode: serverError.defaultMessage
})

export const toFieldValidations = (errorPayload?: ErrorPayload): FieldValidation[] => {
    const fieldValidations: FieldValidation[] = []

    if (!errorPayload) {
        return fieldValidations
    }

    errorPayload.fieldErrors.map(fieldError => {
        fieldValidations.push({
            name: fieldError.field || fieldError.objectName,
            messageCode: fieldError.defaultMessage,
        })
    })

    return fieldValidations

}

