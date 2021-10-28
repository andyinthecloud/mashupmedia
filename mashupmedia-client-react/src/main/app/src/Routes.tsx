import {Route, Switch} from "react-router-dom";
import Introduction from "./components/Introduction";
import NetworkForm from "./settings/NetworkForm";
import PrivateRoute from "./security/PrivateRoute";
import LogIn from "./security/LogIn";

const Routes = () => {

    return (
        <Switch>

            <Route exact path="/">
                <Introduction />
            </Route>
            <Route exact path="/login">
                <LogIn />
            </Route>

            <PrivateRoute component={NetworkForm} path="/settings/network" exact/>


            {/*<Route exact path="/settings/network">*/}
            {/*    <NetworkForm />*/}
            {/*</Route>*/}


        </Switch>
    )
}

export default Routes