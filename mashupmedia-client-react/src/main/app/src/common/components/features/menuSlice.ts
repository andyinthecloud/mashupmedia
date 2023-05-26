import { createSlice } from "@reduxjs/toolkit";


export type MenuPayload = {
    isOpen: boolean 
}


const initialState: MenuPayload = ({isOpen: false})

const menuSlice = createSlice({
    name: 'common/components/menu',
    initialState,
    reducers: {
        openMenu(state) {
            state.isOpen = true
        },
        closeMenu(state) {
            state.isOpen = false
        }
    }
})

export const { openMenu, closeMenu} = menuSlice.actions


export default menuSlice