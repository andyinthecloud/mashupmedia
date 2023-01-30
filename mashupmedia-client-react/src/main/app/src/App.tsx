import { Container, ThemeProvider } from "@mui/material";
import { useSelector } from "react-redux";
import './App.css';
import AlertBoxes from "./common/components/AlertBoxes";
import Footer from "./common/components/Footer";
import Header from "./common/components/Header";
import { RootState } from "./common/redux/store";
import { securityToken } from "./common/security/securityUtils";
import { mashupTheme } from "./common/utils/formUtils";
import { MashupRoutes } from "./MashupRoutes";

function App() {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const hasUserToken = (): boolean => (
        securityToken(userToken) ? true : false
    )

    return (
        <ThemeProvider theme={mashupTheme}>
            <div className={"App"}>
                <Header />
                <Container
                    className="main-container"
                    maxWidth="md"
                    sx={{
                        mt: hasUserToken() ? 24 : 10
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
