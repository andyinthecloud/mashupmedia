import { 
	Component,
	NgModule
} from '@angular/core';

import {
    MatSidenavModule,
    MatSidenavContent,
    MatSidenav
} from '@angular/material';

import {
    BrowserAnimationsModule
} from '@angular/platform-browser/animations';

@NgModule({
    imports: [
        BrowserAnimationsModule,
        MatSidenav
    ],
    exports: [
        BrowserAnimationsModule,
        MatSidenav
    ]
})

@Component({
  selector: 'app-root, sidenav-open-close-example',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mashupmedia-apps';
  
  /*
  events: string[] = [];
  opened: boolean;

  shouldRun = true;
  */
  
}
