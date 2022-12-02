import { Container, ThemeProvider } from "@mui/material";
import { useDispatch } from "react-redux";
import './App.css';
import AlertBoxes from "./common/components/AlertBoxes";
import Drawer from "./common/components/Drawer";
import Footer from "./common/components/Footer";
import AudioPlayer from "./common/components/media/AudioPlayer";
import { addNotification, NotificationType } from "./common/notification/notificationSlice";
import { mashupTheme } from "./common/utils/formUtils";
import { MashupRoutes } from "./MashupRoutes";

function App() {

    const dispatch = useDispatch()

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
                <Drawer></Drawer>
                <AudioPlayer />
                <Container className="main-container" maxWidth="md">
                    <AlertBoxes />
                    <MashupRoutes />
                </Container>
                <Footer />
            </div>
        </ThemeProvider>
    );
}

export default App;
