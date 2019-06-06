import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { slideInAnimation } from './animations';
import { AuthService }      from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.css'],
  animations: [ slideInAnimation ]
})
export class AppComponent implements OnInit{
  events: string[] = [];
  opened: boolean;
  // showSecureContent = false;
  title = 'Tour of Heroes';


  constructor(public authService: AuthService) {}

  ngOnInit() {
  }

  getShowSecureContent() {
    return false;
  }

  getAnimationData(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }
}
