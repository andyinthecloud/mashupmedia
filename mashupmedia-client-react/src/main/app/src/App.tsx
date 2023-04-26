import { Container, ThemeProvider } from "@mui/material";
import { useEffect, useState } from "react";
import './App.css';

import { MashupRoutes } from "./MashupRoutes";
import AlertBoxes from "./common/components/AlertBoxes";
import Footer from "./common/components/Footer";
import Header from "./common/components/Header";
import { isLoggedIn } from "./common/security/backend/loginCalls";
import { mashupTheme } from "./common/utils/formUtils";
import { useSelector } from "react-redux";
import { RootState } from "./common/redux/store";

// type AppPayload = {
//     loggedIn: boolean
// }

function App() {

    // const userToken = useSelector((state: RootState) => state.security.payload?.token)

    // const [props, setProps] = useState<AppPayload>({
    //     loggedIn: false
    // })

    // useEffect(() => {
    //     isLoggedIn()
    //         .then((response) => {
    //             setProps({ loggedIn: response.parsedBody || false })
    //         })
    // }, [userToken])

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
