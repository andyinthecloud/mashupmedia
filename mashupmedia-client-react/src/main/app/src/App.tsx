import { Container, ThemeProvider } from "@mui/material";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import './App.css';

import AlertBoxes from "./common/components/AlertBoxes";
import Footer from "./common/components/Footer";
import Header from "./common/components/Header";
import { RootState } from "./common/redux/store";
import { mashupTheme } from "./common/utils/formUtils";
import { MashupRoutes } from "./MashupRoutes";
import { hasPlaylist } from "./media/music/rest/playlistCalls";



type AppPayload = {
    loggedIn: boolean
}

function App() {


    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<AppPayload>({
        loggedIn: false
    })

    useEffect(() => {
        hasPlaylist(userToken).then(response => {
            if (response.ok) {
                setProps({ loggedIn: response.parsedBody?.payload ? true : false })
            } else {
                setProps({ loggedIn: false })
            }
        })
    }, [userToken])

    return (
        <ThemeProvider theme={mashupTheme}>
            <div className={"App"}>
                <Header />
                <Container
                    className="main-container"
                    maxWidth="md"
                    sx={{
                        mt: props.loggedIn ? 26 : 10
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
