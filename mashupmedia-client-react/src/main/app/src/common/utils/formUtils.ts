import { createTheme } from '@mui/material/styles';

export interface NameValue {
    name: string
    value: string | number
}

export const mashupTheme = createTheme({

    palette: {
        primary: {
            main: '#e579e9',
            contrastText: '#ffffff'
        },
        secondary: {
            main: '#7de979',
            contrastText: '#ffffff',
        }
    }
}
)

export const isEnterKey = (code: string): boolean => {    
    if (code && code === "Enter" || code === "NumpadEnter") {
        return true
    } else {
        return false
    }
}

export const isInArray = <T>(items: T[] | undefined, item: T): boolean => {
    if (!items) {
        return false
    }

    return items.some(v => v === item)

}












