import { Component, TemplateRef, ViewChild }        from '@angular/core';
import { Router,
         NavigationExtras } from '@angular/router';
import { AuthService }      from '../auth.service';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],

})
export class LoginComponent {
  message: string;
  username = new FormControl('');
  password = new FormControl('');


  constructor(public authService: AuthService, public router: Router) {
    //this.setMessage();
  }

  setMessage() {
    this.message = 'Logged ' + (this.authService.isLoggedIn ? 'in' : 'out');
  }

  login() {
    this.message = 'Trying to log in ...';
    console.log('Is logged in = ' + this.authService.isLoggedIn);
    console.log('username: ' + this.username.value + ', password: ' + this.password.value);

    this.authService.login(this.username.value, this.password.value);

    // let isLoggedIn: boolean = false;

    this.authService.login(this.username.value, this.password.value).subscribe(isLoggedIn => {
      if (!isLoggedIn) {
        this.message = '*Invalid username / password combination.';
        return;
      }

      this.setMessage();
      if (this.authService.isLoggedIn) {
        // Get the redirect URL from our auth service
        // If no redirect has been set, use the default
        let redirect = this.authService.redirectUrl ? this.router.parseUrl(this.authService.redirectUrl) : '/admin';

        // Set our navigation extras object
        // that passes on our global query params and fragment
        let navigationExtras: NavigationExtras = {
          queryParamsHandling: 'preserve',
          preserveFragment: true
        };

        // Redirect the user
        this.router.navigateByUrl(redirect, navigationExtras);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.setMessage();
  }
}
