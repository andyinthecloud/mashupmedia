import { ThemeProvider } from "@mui/styles";
import { createStore } from "@reduxjs/toolkit";
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import App from './App';
import './index.css';
import { store } from "./redux/store";
import reportWebVitals from './reportWebVitals';
import { mashupTheme } from "./utils/formUtils";


// const store = createStore(rootReducer);

ReactDOM.render(

    
    <React.StrictMode>
        <Provider store={store}>
            <ThemeProvider theme={mashupTheme}>
                <BrowserRouter>
                <App />
                </BrowserRouter>
            </ThemeProvider>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
