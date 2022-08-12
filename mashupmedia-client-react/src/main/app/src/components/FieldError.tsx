import './FieldError.css';
import { Warning } from "@mui/icons-material";
import { useEffect, useState } from "react";
import { fieldValidation, FieldValidation, FormValidation } from "../utils/form-validation-utils";


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
            {fieldValidationState?.message.length &&
                <span>
                    <Warning fontSize='small'></Warning>
                    {fieldValidationState?.message}
                </span>
            }
        </div>
    )

}


export default FieldError