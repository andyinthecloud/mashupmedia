import './Footer.css';


const Footer = () => {

    const currentYear = new Date().getFullYear();


    return (
        <footer>
            Copyright Mashup Media {currentYear}
        </footer>
    )
}

export default Footer