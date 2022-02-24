import { Route, Switch } from "react-router-dom";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import PrivateRoute from "./security/PrivateRoute";
import MyAccount from "./settings/MyAccount";
import NetworkForm from "./settings/NetworkForm";

const Routes = () => {

    return (
        <Switch>

            <Route exact path="/">
                <Introduction />
            </Route>
            <Route exact path="/login">
                <LogIn/>
            </Route>
            <PrivateRoute component={NetworkForm} path="/settings/network" exact/>
            <PrivateRoute component={MyAccount} path="/settings/my-account" exact/>


            {/*<Route exact path="/settings/network">*/}
            {/*    <NetworkForm />*/}
            {/*</Route>*/}


        </Switch>
    )
}

export default Routes