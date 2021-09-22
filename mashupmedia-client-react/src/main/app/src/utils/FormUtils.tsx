
export interface NameValue {
    name: string;
    value: string|number;
}


export const getNameValueFromEvent = (event: any): NameValue => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;

    // this.setState({
    //     [name]: value
    // });

    return {
        name,
        value
    };
}