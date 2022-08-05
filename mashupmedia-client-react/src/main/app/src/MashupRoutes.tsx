import { Login } from "@mui/icons-material";
import { Route, Routes } from "react-router-dom";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import { RequireAuthenication } from "./security/RequireAuthentication";
import ChangeUserPassword from "./settings/ChangeUserPassword";
import MyAccount from "./settings/MyAccount";
import NetworkForm from "./settings/NetworkForm";


export function MashupRoutes() {
    return (

        <Routes>
            <Route path="/" element={<Introduction />} />
            <Route path="/login" element={<LogIn />} />

            <Route path="/settings">
                <Route index element={
                    <RequireAuthenication>
                        <MyAccount />
                    </RequireAuthenication>
                } />
                <Route path="my-account" element={
                    <RequireAuthenication>
                        <MyAccount />
                    </RequireAuthenication>
                } />

                <Route path="change-user-password">
                    <Route index element={
                        <RequireAuthenication>
                            <ChangeUserPassword />
                        </RequireAuthenication>
                    } />
                    <Route path=":userId" element={
                        <RequireAuthenication>
                            <ChangeUserPassword />
                        </RequireAuthenication>
                    } />

                </Route>

                <Route path="network" element={
                    <RequireAuthenication>
                        <NetworkForm />
                    </RequireAuthenication>
                } />
            </Route>








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
