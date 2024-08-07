import './FieldError.css';
import { Warning } from "@mui/icons-material";
import { useEffect, useState } from "react";
import { fieldValidation, FieldValidation, FormValidation } from "../utils/formValidationUtils";
import { t } from "i18next";


export type FieldErrorPayload = {
    fieldName: string
    formValidation: FormValidation
}


const FieldError = (payload: FieldErrorPayload) => {
    const [props] = useState<FieldErrorPayload>(payload)
    const [fieldValidationState, setFieldValidationState] = useState<FieldValidation>()

    useEffect(() => {
        setFieldValidationState(fieldValidation(props.fieldName, props.formValidation))
    })

    return (
        <div className='FieldError'>
            {fieldValidationState?.messageCode.length &&
                <span>
                    <Warning fontSize='small'></Warning>
                    {t(fieldValidationState?.messageCode)}
                </span>
            }
        </div>
    )

}


export default FieldError