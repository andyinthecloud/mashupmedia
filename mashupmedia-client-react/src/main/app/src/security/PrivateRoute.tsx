import {Redirect, Route, RouteProps} from "react-router-dom";
import {isLogin} from "./SecurityUtils";

interface PrivateRouteProps extends RouteProps {
    // tslint:disable-next-line:no-any
    component: any;
    // isSignedIn: boolean;
}

const PrivateRoute = (props: PrivateRouteProps) => {
    const { component: Component, ...rest } = props;

    return (
        <Route
            {...rest}
            render={(routeProps) =>
                isLogin() ? (
                    <Component {...routeProps} />
                ) : (
                    <Redirect
                        to={{
                            pathname: '/login',
                            state: { from: routeProps.location }
                        }}
                    />
                )
            }
        />
    );
};

// const PrivateRoute = ({component: any, ...rest}) => {
//     return (
//
//         // Show the component only when the user is logged in
//         // Otherwise, redirect the user to /signin page
//         <Route {...rest} render={props => (
//             isLogin() ?
//                 <Component {...props} />
//                 : <Redirect to="/signin" />
//         )} />
//     );
// };

export default PrivateRoute;