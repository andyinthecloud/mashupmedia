import { Route, Routes } from "react-router-dom";
import Introduction from "./common/components/Introduction";
import LogIn from "./common/security/LogIn";
import { RequireAuthenication } from "./common/security/RequireAuthentication";
import ChangeUserPassword from "./configuration/ChangeUserPassword";
import Groups from "./configuration/Groups";
import NetworkForm from "./configuration/NetworkForm";
import User from "./configuration/User";
import Users from "./configuration/Users";
import Group from "./configuration/Group";
import Libraries from "./configuration/Libraries";
import Library from "./configuration/Library";
import { ConfigurationRoutes } from './configuration/ConfigurationRoutes';
import { MediaRoutes } from './media/MediaRoutes';
import Albums from './media/music/Albums';
import Artists from './media/music/Artists';
import Artist from './media/music/Artist';


export const MashupRoutes = () => {
    return (
        <Routes>
            <Route path="/" element={<Introduction />} />
            <Route path="/login" element={<LogIn />} />

            <Route path="/configuration">
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


                <Route path="libraries" element={
                    <RequireAuthenication>
                        <Libraries />
                    </RequireAuthenication>
                } />


                <Route path="library">
                    <Route index element={
                        <RequireAuthenication>
                            <Library />
                        </RequireAuthenication>
                    } />
                    <Route path=":libraryId" element={
                        <RequireAuthenication>
                            <Library />
                        </RequireAuthenication>
                    } />
                </Route>
            </Route>

            <Route path="/music">
                <Route path="artists" element={
                    <RequireAuthenication>
                        <Artists />
                    </RequireAuthenication>
                } />

                <Route path="artist">
                    <Route path=":artistId" element={
                        <RequireAuthenication>
                            <Artist />
                        </RequireAuthenication>
                    } />
                </Route>

                <Route path="albums" element={
                    <RequireAuthenication>
                        <Albums />
                    </RequireAuthenication>
                } />
            </Route>
        </Routes>
    )

}
