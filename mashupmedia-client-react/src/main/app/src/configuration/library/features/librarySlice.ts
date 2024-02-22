import { createSlice, PayloadAction } from "@reduxjs/toolkit"

export type LibraryRefreshPayload = {
    triggerSave: number | null
}

const initialState: LibraryRefreshPayload = {
    triggerSave: null
}

const librarySlice = createSlice({
    name: 'configuration/library/folder',
    initialState,
    reducers: {
        triggerSaveLibrary: (state, action: PayloadAction<LibraryRefreshPayload>) => {
            state.triggerSave = action.payload.triggerSave
        }

    }
})


export const {triggerSaveLibrary} = librarySlice.actions

export default librarySlice