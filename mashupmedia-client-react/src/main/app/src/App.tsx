import { Container, ThemeProvider } from "@mui/material";
import './App.css';

import { MashupRoutes } from "./MashupRoutes";
import AlertBoxes from "./common/components/AlertBoxes";
import Footer from "./common/components/Footer";
import Header from "./common/components/Header";
import { mashupTheme } from "./common/utils/formUtils";


function App() {

    return (
        <ThemeProvider theme={mashupTheme}>
            <div className={"App"}>
                <Header />
                <Container
                    className="main-container"
                    maxWidth="md"
                    sx={{
                        mt: 15
                    }}
                >
                    <AlertBoxes />
                    <MashupRoutes />
                </Container>
                <Footer />
            </div>
        </ThemeProvider>
    );
}

export default App
