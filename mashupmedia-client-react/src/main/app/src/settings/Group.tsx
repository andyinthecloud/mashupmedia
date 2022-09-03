import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { RootState } from "../redux/store";
import { FormValidation } from "../utils/form-validation-utils";
import { getGroup, getGroups, NameValuePayload } from "./backend/metaCalls";

type GroupValidationPayload = {
    groupPayload: NameValuePayload<number>
    formValidation: FormValidation
}

const Group = () => {

    const enum FieldNames {
        ID_NAME = 'username',
        VALUE = 'name',
    }

    const { groupId } = useParams();
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<GroupValidationPayload>({
        groupPayload: {
            name: '',
            value: 0
        },
        formValidation: { fieldValidations: [] }
    })

    useEffect(() => {

        if (groupId) {
            getGroup(+groupId, userToken)
            .then(response => {
                const groupPayload = response.parsedBody
                ? response.parsedBody
                : null

                if (response.parsedBody) {
                    setProps(p => ({
                        ...p,
                        groupPayload
                    }))
                }
            })
        }

    }, [userToken, groupId] )
    
    return (
        <h1>Group</h1>
    )
}

export default Group