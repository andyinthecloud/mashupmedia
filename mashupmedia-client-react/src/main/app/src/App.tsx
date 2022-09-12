import { Button, Container, ThemeProvider } from "@mui/material";
import { useDispatch } from "react-redux";
import './App.css';
import AlertBoxes from "./common/components/AlertBoxes";
import Drawer from "./common/components/Drawer";
import Footer from "./common/components/Footer";
import Header from "./common/components/Header";
import { MashupRoutes } from "./MashupRoutes";
import { addNotification, NotificationType } from "./common/notification/notificationSlice";
import { mashupTheme } from "./common/utils/formUtils";

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
            <Button onClick={handleClick}>alert</Button>
            <div className={"App"}>
                <Drawer></Drawer>
                <Header />
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
