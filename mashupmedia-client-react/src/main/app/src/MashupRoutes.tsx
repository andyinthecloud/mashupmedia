import { Route, Routes } from "react-router-dom";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import { RequireAuthenication } from "./security/RequireAuthentication";
import ChangeUserPassword from "./settings/ChangeUserPassword";
import Group from "./settings/Group";
import Groups from "./settings/Groups";
import NetworkForm from "./settings/NetworkForm";
import User from "./settings/User";
import Users from "./settings/Users";


export function MashupRoutes() {
    return (

        <Routes>
            <Route path="/" element={<Introduction />} />
            <Route path="/login" element={<LogIn />} />

            <Route path="/settings">
                <Route index element={
                    <RequireAuthenication>
                        <User />
                    </RequireAuthenication>
                } />
                <Route path="my-account" element={
                    <RequireAuthenication>
                        <User />
                    </RequireAuthenication>
                } />

                <Route path="new-account" element={
                    <RequireAuthenication>
                        <User />
                    </RequireAuthenication>
                } />

                <Route path="user-account">
                    <Route index element={
                        <RequireAuthenication>
                            <User />
                        </RequireAuthenication>
                    } />
                    <Route path=":userId" element={
                        <RequireAuthenication>
                            <User />
                        </RequireAuthenication>
                    } />
                </Route>

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

                <Route path="users" element={
                    <RequireAuthenication>
                        <Users />
                    </RequireAuthenication>
                } />

                <Route path="groups" element={
                    <RequireAuthenication>
                        <Groups />
                    </RequireAuthenication>
                } />

                <Route path="group">
                    <Route index element={
                        <RequireAuthenication>
                            <Group />
                        </RequireAuthenication>
                    } />
                    <Route path=":groupId" element={
                        <RequireAuthenication>
                            <Group />
                        </RequireAuthenication>
                    } />
                </Route>

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
