type ErrorBoxProps = {
    message: string;
}

const ErrorBox = (props: ErrorBoxProps) => {
    return (
        <div> {props.message} </div>
    )
}


export default ErrorBox