import { useSelector } from "react-redux";
import { Redirect, Route, RouteProps } from "react-router-dom";
import { RootState } from "../redux/store";
import { LogInState } from "./features/loggedInUserSlice";
import { isLoggedIn } from "./SecurityUtils";

interface PrivateRouteProps extends RouteProps {
    // tslint:disable-next-line:no-any
    component: any;
}


const PrivateRoute = (props: PrivateRouteProps) => {
    const { component: Component, ...rest } = props;

    const logInState = useSelector<RootState, LogInState>(state => state.loggedInUser);


    return (
        <Route
            {...rest}
            render={(routeProps) =>
                isLoggedIn(logInState) ? (
                    <Component {...routeProps} />
                ) : (          

                    <Redirect to="/login" />
                )
            }
        />
    )
}

export default PrivateRoute;