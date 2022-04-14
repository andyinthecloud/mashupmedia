import { Button, Container, ThemeProvider } from "@mui/material";
import { Route, Routes } from "react-router-dom";
import './App.css';
import Drawer from "./components/Drawer";
import Footer from "./components/Footer";
import Header from "./components/Header";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import { RequireAuthenication } from "./security/RequireAuthentication";
import MyAccount from "./settings/MyAccount";
import NetworkForm from "./settings/NetworkForm";
import { mashupTheme } from "./utils/formUtils";


function App() {

    return (
        <ThemeProvider theme={mashupTheme}>
            <Button variant="contained" color="primary">
                Primary
            </Button>
            <Button variant="contained" color="secondary">
                Secondary
            </Button>

            <div className={"App"}>

                <Drawer></Drawer>
                <Header />
                <Container className="main-container" maxWidth="md">

                    <Routes>
                        <Route path="/" element={<Introduction />} />
                        <Route path="/login" element={<LogIn />} />

                        <Route path="/settings/my-account" element={
                            <RequireAuthenication>
                                <MyAccount />
                            </RequireAuthenication>
                        } />


                        <Route path="/settings/network" element={
                            <RequireAuthenication>
                                <NetworkForm />
                            </RequireAuthenication>
                        } />
                    </Routes>


                </Container>
                <Footer />

            </div>



        </ThemeProvider>
    );
}

export default App;
