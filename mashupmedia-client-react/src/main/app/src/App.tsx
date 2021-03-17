import './App.css';

import Header from "./components/Header";
import {Container} from "@material-ui/core";
import Drawer from "./components/Drawer";

import Footer from "./components/Footer";
import Introduction from "./components/Introduction";

function App() {
  return (
    <div className="App">

        <Drawer></Drawer>

        <Container >
            <Header />
            <Introduction />
            <Footer />
        </Container>


    </div>
  );
}

export default App;
