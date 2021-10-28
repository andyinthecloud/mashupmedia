import {Component} from "react";

export interface ErrorBoxProps {
    message: string;
}

export class ErrorBox extends Component<ErrorBoxProps, {}> {
    render() {
        const {message} = this.props;
        return (
            <div> {message} </div>
        )

    }
}