import { Route, Routes } from "react-router-dom";
import Introduction from "./common/components/Introduction";
import LogIn from "./common/security/LogIn";
import LogOut from "./common/security/LogOut";
import { RequireAuthenication } from "./common/security/RequireAuthentication";
import ResetPassword from "./configuration/ResetPassword";
import Encode from "./configuration/Encode";
import Group from "./configuration/Group";
import Groups from "./configuration/Groups";
import Libraries from "./configuration/Libraries";
import Library from "./configuration/Library";
import User from "./configuration/User";
import Users from "./configuration/Users";
import Album from "./media/music/Album";
import Albums from './media/music/Albums';
import Artist from './media/music/Artist';
import Artists from './media/music/Artists';
import Playlists from "./media/playlist/Playlists";
import MusicPlaylist from "./media/playlist/music/MusicPlaylist";
import SelectMusicPlaylist from "./media/playlist/music/SelectMusicPlaylist";
import MediaSearch from "./media/search/MediaSearch";
import CreateUser from "./configuration/CreateUser";
import ActivateUser from "./configuration/ActivateUser";


export const MashupRoutes = () => {
    return (
        <Routes>
            <Route path="/" element={<Introduction />} />
            <Route path="/login" element={<LogIn />} />
            <Route path="/logout" element={<LogOut />} />
            <Route path="/create-user" element={<CreateUser />} />
            <Route path="/create-user/activate" element={<ActivateUser />} />
            <Route path="/reset-password" element={<ResetPassword />} />

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

                <Route path="encode" element={
                    <RequireAuthenication>
                        <Encode />
                    </RequireAuthenication>
                } />

            </Route>


            <Route path="/playlists">

                <Route path="all" element={
                    <RequireAuthenication>
                        <Playlists />
                    </RequireAuthenication>
                } />

                <Route path="music">
                    <Route path=":playlistId" element={
                        <RequireAuthenication>
                            <MusicPlaylist />
                        </RequireAuthenication>
                    } />

                    <Route path="select" element={
                        <RequireAuthenication>
                            <SelectMusicPlaylist />
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

                <Route path="album">
                    <Route path=":albumId" element={
                        <RequireAuthenication>
                            <Album />
                        </RequireAuthenication>
                    } />
                </Route>
            </Route>

            <Route path="/search">
                <Route path="media" element={
                    <RequireAuthenication>
                        <MediaSearch />
                    </RequireAuthenication>
                } />
            </Route>

        </Routes>
    )

}
