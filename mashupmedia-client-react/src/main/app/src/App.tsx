import './App.css';

import Header from "./components/Header";
// import {Container} from "@material-ui/core";
import Drawer from "./components/Drawer";

import Footer from "./components/Footer";
import {BrowserRouter} from "react-router-dom";
import Routes from "./Routes";
import {Container} from "@mui/material";

function App() {
  return (

<BrowserRouter>

    <div className="App">

        <Drawer ></Drawer>

        <Header />
        <Container className="main-container" maxWidth="md">
            <Routes />
        </Container>
        <Footer />


    </div>

</BrowserRouter>
  );
}

export default App;
