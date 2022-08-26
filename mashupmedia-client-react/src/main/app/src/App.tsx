import { Button, Container, ThemeProvider } from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import './App.css';
import AlertBoxes from "./components/AlertBoxes";
import Drawer from "./components/Drawer";
import Footer from "./components/Footer";
import Header from "./components/Header";
import { MashupRoutes } from "./MashupRoutes";
import { addNotification, NotificationType } from "./notification/notificationSlice";
import { RootState } from "./redux/store";
import { mashupTheme } from "./utils/formUtils";


const userDetailContext = React.createContext(null);

function App() {


    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)



    const dispatch = useDispatch()

    // useEffect(() => {
    //     dispatch(
    //         myAccount(userToken)
    //     )

    // }, [dispatch, userToken])


    // const userPayloadState = useSelector((state: RootState) => state.security.payload)

    // const [props, setProps] = useState<UserPayload>()

    // useEffect(() => {
    //     if (userPayloadState) {
    //         setProps(userPayloadState)
    //     }

    // }, [userPayloadState])


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

                <pre>user: {JSON.stringify(userPolicyPayload)}</pre>

                <Container className="main-container" maxWidth="md">
                    {/* <AlertBoxes notificationPayloads={notificationPayloadsState.notificationPayloads}></AlertBoxes> */}
                    <AlertBoxes />
                    <MashupRoutes />
                </Container>
                <Footer />
            </div>
        </ThemeProvider>
    );
}

export default App;
