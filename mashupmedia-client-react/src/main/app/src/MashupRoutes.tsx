import { Route, Routes } from "react-router-dom";
import Introduction from "./common/components/Introduction";
import LogIn from "./common/security/LogIn";
import LogOut from "./common/security/LogOut";
import { RequireAuthenication } from "./common/security/RequireAuthentication";
import ActivateUser from "./configuration/ActivateUser";
import CreateUser from "./configuration/CreateUser";
import Libraries from "./configuration/library/Libraries";
import Library from "./configuration/library/Library";
import ResetPassword from "./configuration/ResetPassword";
import User from "./configuration/User";
import Users from "./configuration/Users";
import Album from "./media/music/Album";
import Albums from './media/music/Albums';
import Artist from './media/music/Artist';
import Artists from './media/music/Artists';
import EditAlbum from "./media/music/EditAlbum";
import EditArtist from "./media/music/EditArtist";
import UploadArtistTracks from "./media/music/UploadArtistTracks";
import MusicPlaylist from "./media/playlist/music/MusicPlaylist";
import SelectMusicPlaylist from "./media/playlist/music/SelectMusicPlaylist";
import Playlists from "./media/playlist/Playlists";
import MediaSearch from "./media/search/MediaSearch";
import TrackPlaying from "./media/music/TrackPlaying";


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


            <Route path="/playlists">

                <Route path="all" element={
                    <RequireAuthenication>
                        <Playlists />
                    </RequireAuthenication>
                } />



                <Route path="music">

                    <Route path="select" element={
                        <RequireAuthenication>
                            <SelectMusicPlaylist />
                        </RequireAuthenication>
                    } />


                    <Route path="playing" element={
                        <RequireAuthenication>
                            <TrackPlaying />
                        </RequireAuthenication>
                    } />


                    <Route path=":playlistId" element={
                        <RequireAuthenication>
                            <MusicPlaylist />
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
                    <Route index element={
                        <RequireAuthenication>
                            <Artist />
                        </RequireAuthenication>
                    } />
                    <Route path=":artistId" element={
                        <RequireAuthenication>
                            <Artist />
                        </RequireAuthenication>
                    } />

                    <Route path="edit">
                        <Route path=":artistId" element={
                            <RequireAuthenication>
                                <EditArtist />
                            </RequireAuthenication>
                        } />
                    </Route>

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

                    <Route path="edit">
                        <Route path=":albumId" element={
                            <RequireAuthenication>
                                <EditAlbum />
                            </RequireAuthenication>
                        } />
                    </Route>

                </Route>

                <Route path="upload-artist-tracks" element={
                    <RequireAuthenication>
                        <UploadArtistTracks />
                    </RequireAuthenication>
                } />

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
