import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { tap, delay, map } from 'rxjs/operators';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { User, AuthConstants } from './user';
import { catchError } from 'rxjs/operators';
import { ErrorHandlerService, HandleError } from '../error-handler/error-handler.service';


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
    //'Authorization': 'my-auth-token'
  })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private baseUrl = environment.baseUrl;
  private loginUrl = this.baseUrl + 'security/login';
  private handleError: HandleError;

  // store the URL so we can redirect after logging in
  redirectUrl: string;

  constructor(private http: HttpClient, errorHandlerService: ErrorHandlerService) {
    this.handleError = errorHandlerService.createHandleError('AuthService');

  }

  isLoggedIn(): boolean {
    let currentUserValue = localStorage.getItem(AuthConstants.CURRENT_USER);
    if (!currentUserValue) {
      return false;
    }

    let user:User = JSON.parse(currentUserValue);
    if (user) {
      return true;
    }

    return false;
  }

  login(username: string, password: string): Observable<boolean> {

    let body = new HttpParams();
    body = body.append('username', username);
    body = body.append('password', password);

    let user = new User();
    user.username = username;
    user.password = password;

    return this.http.post<boolean>(this.loginUrl, user).pipe(map((response => {
      if (!response) {
        return false;
      }

      let userValue = JSON.stringify(response);
      console.log(userValue);
      localStorage.setItem(AuthConstants.CURRENT_USER, userValue);
      return true;
    })));

  }

  logout(): void {
    localStorage.setItem(AuthConstants.CURRENT_USER, null);
  }



}
