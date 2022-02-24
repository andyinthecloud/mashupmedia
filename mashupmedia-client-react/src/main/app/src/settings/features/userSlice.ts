import { PayloadState } from "../../redux/store"

export type UserPayload = {
    admin: boolean;
    enabled: boolean;
    username: string;
    name: string;
}

const initialState: PayloadState<UserPayload> = {
    payload: {
        admin: false,
        enabled: false,
        username: '',
        name: ''
    },
    loading: false,
    error: null
}

const userUrl: string = (process.env.REACT_APP_MASHUPMEDIA_BACKEND_URL as string) + '/api/admin/user/'