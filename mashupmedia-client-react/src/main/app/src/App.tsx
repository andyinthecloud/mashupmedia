import { Container, ThemeProvider } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import './App.css';
import AlertBoxes from "./common/components/AlertBoxes";
import Drawer from "./common/components/MenuDrawer";
import Footer from "./common/components/Footer";
import AudioPlayer from "./common/components/media/AudioPlayer";
import { addNotification, NotificationType } from "./common/notification/notificationSlice";
import { RootState } from "./common/redux/store";
import { mashupTheme } from "./common/utils/formUtils";
import { MashupRoutes } from "./MashupRoutes";
import { securityToken } from "./common/security/securityUtils"
import MashupBar from "./common/components/MashupBar";

function App() {

    const userToken = useSelector((state: RootState) => state.security.payload?.token)
    const dispatch = useDispatch()
    const hasUserToken = (): boolean => (
        securityToken(userToken) ? true : false
    )

    const handleClick = () => {
        dispatch(
            addNotification({
                message: 'test',
                notificationType: NotificationType.SUCCESS
            })
        )
    }

    return (
        <ThemeProvider theme={mashupTheme}>
            <div className={"App"}>
                <MashupBar />
                {/* <Drawer></Drawer> */}
                <Container className="main-container" maxWidth="md">
                    {hasUserToken() && <AudioPlayer />}
                    <AlertBoxes />
                    <MashupRoutes />
                </Container>
                <Footer />
            </div>
        </ThemeProvider>
    );
}

export default App;
