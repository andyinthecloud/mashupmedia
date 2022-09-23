import { Group, Groups } from '@mui/icons-material'
import { Route } from 'react-router-dom'
import { RequireAuthenication } from '../common/security/RequireAuthentication'
import ChangeUserPassword from './ChangeUserPassword'
import Libraries from './Libraries'
import Library from './Library'
import NetworkForm from './NetworkForm'
import User from './User'
import Users from './Users'

export const ConfigurationRoutes = () => {
    return (
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
    )
}



