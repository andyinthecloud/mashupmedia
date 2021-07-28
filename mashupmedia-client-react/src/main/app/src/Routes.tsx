import {Route, Switch} from "react-router-dom";
import Introduction from "./components/Introduction";
import NetworkForm from "./settings/NetworkForm";

const Routes = () => {

    return (
        <Switch>
            <Route exact path="/">
                <Introduction />
            </Route>
            <Route exact path="/settings/network">
                <NetworkForm />
            </Route>

        </Switch>
    )
}

export default Routes