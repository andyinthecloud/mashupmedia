import { NgModule }       from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { Router } from '@angular/router';

import { AppComponent }            from './app.component';
import { PageNotFoundComponent }   from './page-not-found/page-not-found.component';
import { ComposeMessageComponent } from './compose-message/compose-message.component';

import { AppRoutingModule }        from './app-routing.module';
import { HeroesModule }            from './heroes/heroes.module';

import { HttpClientModule } from '@angular/common/http';
import { AuthModule } from './auth/auth.module';
import { HomeComponent } from './home/home.component';
import { MaterialModule } from './material-module';
import { AdminRoutingModule } from './admin/admin-routing.module';
import { AdminModule } from './admin/admin.module';
import { AdminLayoutComponent } from './base-layout/admin-layout/admin-layout.component';
import { BaseLayoutComponent } from './base-layout/base-layout.component';
import { HomeLayoutComponent } from './base-layout/home-layout/home-layout.component';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HeroesModule,
    AppRoutingModule,
    HttpClientModule,
    AuthModule,
    MaterialModule,
    AdminModule,
    AdminRoutingModule
  ],
  declarations: [
    AppComponent,
    ComposeMessageComponent,
    PageNotFoundComponent,
    HomeComponent,
    HomeLayoutComponent,
    // AdminLayoutComponent,


  ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
  // Diagnostic only: inspect router configuration
  constructor(router: Router) {
    // Use a custom replacer to display function names in the route configs
    // const replacer = (key, value) => (typeof value === 'function') ? value.name : value;

    // console.log('Routes: ', JSON.stringify(router.config, replacer, 2));
  }
}
