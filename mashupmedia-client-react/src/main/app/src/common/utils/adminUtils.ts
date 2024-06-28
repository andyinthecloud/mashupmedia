import { UserPayload } from "../../configuration/backend/userCalls"
import { UserPolicyPayload } from "../security/features/userPolicySlice"

export function isContentEditor(userPayload: UserPayload | undefined, userPolicyPayload: UserPolicyPayload | null): boolean {
    if (!userPayload || !userPolicyPayload) {
        return false
    }

    return userPolicyPayload.administrator || userPayload.username === userPolicyPayload.username
}