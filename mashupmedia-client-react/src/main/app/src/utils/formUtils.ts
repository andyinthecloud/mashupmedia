import {createTheme} from '@mui/material/styles';

export interface NameValue {
    name: string
    value: string | number
}


export const getNameValueFromEvent = (event: any): NameValue => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value
    const name = target.name

    return {
        name,
        value
    }
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
            },
        }
    }
)









