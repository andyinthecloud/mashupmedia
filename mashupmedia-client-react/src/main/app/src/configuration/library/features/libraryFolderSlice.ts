import { createSlice, PayloadAction } from "@reduxjs/toolkit"

export type LibraryFolderRefreshPayload = {
    folderPath: string
    fromParent?: boolean
}

const initialState: LibraryFolderRefreshPayload = {
    folderPath: '',
    fromParent: false
}

const libraryFolderSlice = createSlice({
    name: 'configuration/library/folder',
    initialState,
    reducers: {
        refreshLibraryFolder: (state, action: PayloadAction<LibraryFolderRefreshPayload>) => {
            state.folderPath = action.payload.folderPath
        }

    }
})


export const {refreshLibraryFolder} = libraryFolderSlice.actions

export default libraryFolderSlice