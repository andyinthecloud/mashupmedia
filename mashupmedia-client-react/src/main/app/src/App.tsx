import './App.css';

import Header from "./components/Header";
import Drawer from "./components/Drawer";

import Footer from "./components/Footer";
import {BrowserRouter} from "react-router-dom";
import Routes from "./Routes";
import {Button, Container, ThemeProvider} from "@mui/material";
import {mashupTheme} from "./utils/FormUtils";


function App() {

    return (
        <ThemeProvider theme={mashupTheme}>
            <Button variant="contained" color="primary">
                Primary
            </Button>
            <Button variant="contained" color="secondary">
                Secondary
            </Button>

        <BrowserRouter>
                <div className={"App"}>

                    <Drawer></Drawer>
                    <Header/>
                    <Container className="main-container" maxWidth="md">
                        <Routes/>
                    </Container>
                    <Footer/>

                </div>

        </BrowserRouter>

        </ThemeProvider>
    );
}

export default App;
