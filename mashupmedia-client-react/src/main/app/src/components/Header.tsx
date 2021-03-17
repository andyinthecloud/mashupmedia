import PropTypes from 'prop-types';

const Header = () => {
    return (
        <header>

        </header>
    )
}

Header.defaultProps = {
    title: 'Mashup Media'
}

Header.propTypes = {
    title: PropTypes.string
}

export default Header