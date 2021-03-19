import './App.css';

import Header from "./components/Header";
import {Container} from "@material-ui/core";
import Drawer from "./components/Drawer";

import Footer from "./components/Footer";
import Introduction from "./components/Introduction";

import './App.css';

function App() {
  return (
    <div className="App">

        <Drawer></Drawer>

        <Header />
        <Container className="main-container">
            <Introduction />
        </Container>
        <Footer />


    </div>
  );
}

export default App;
