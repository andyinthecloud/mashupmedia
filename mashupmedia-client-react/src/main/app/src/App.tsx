import { Container, ThemeProvider } from "@mui/material";
import { useSelector } from "react-redux";
import './App.css';
import AlertBoxes from "./common/components/AlertBoxes";
import Footer from "./common/components/Footer";
import MashupBar from "./common/components/MashupBar";
import AudioPlayer from "./common/components/media/AudioPlayer";
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
                <MashupBar />
                {hasUserToken() && <AudioPlayer />}
                <Container
                    className="main-container"
                    maxWidth="md"
                    sx={{
                        mt: hasUserToken() ? 10 : 0
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
