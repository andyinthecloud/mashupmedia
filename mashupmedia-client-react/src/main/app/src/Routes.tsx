import { Route, Switch } from "react-router-dom";
import Introduction from "./components/Introduction";
import LogIn from "./security/LogIn";
import PrivateRoute from "./security/PrivateRoute";
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
            <Route exact path="/test" component={NetworkForm} />

            <PrivateRoute component={NetworkForm} path="/settings/network" exact/>


            {/*<Route exact path="/settings/network">*/}
            {/*    <NetworkForm />*/}
            {/*</Route>*/}


        </Switch>
    )
}

export default Routes