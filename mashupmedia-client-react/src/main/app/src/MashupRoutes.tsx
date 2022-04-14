import { Route, Routes } from "react-router-dom";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import { RequireAuthenication } from "./security/RequireAuthentication";
import MyAccount from "./settings/MyAccount";
import NetworkForm from "./settings/NetworkForm";


function MashupRoutes() {
    return (

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




            {/* <Route path="/settings">
                <Route index={true} element={<Introduction />}></Route>
                <Route path="/settings/network" element={
                    <RequireAuthenication>
                        <NetworkForm />
                    </RequireAuthenication>
                } />
                <Route path="/settings/my-account" element={
                    <RequireAuthenication>
                        <MyAccountComponent />
                    </RequireAuthenication>
                } />

            </Route> */}

        </Routes>
    )

}
