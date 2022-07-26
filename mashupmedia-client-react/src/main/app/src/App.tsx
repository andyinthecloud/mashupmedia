import { Container, ThemeProvider } from "@mui/material";
import './App.css';
import Drawer from "./components/Drawer";
import Footer from "./components/Footer";
import Header from "./components/Header";
import { MashupRoutes } from "./MashupRoutes";
import { mashupTheme } from "./utils/formUtils";


function App() {

    return (
        <ThemeProvider theme={mashupTheme}>

            <div className={"App"}>

                <Drawer></Drawer>
                <Header />
                <Container className="main-container" maxWidth="md">
                    <MashupRoutes />
                </Container>
                <Footer />

            </div>



        </ThemeProvider>
    );
}

export default App;
