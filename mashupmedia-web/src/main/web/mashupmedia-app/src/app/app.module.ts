import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';


import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
//import { MatInputModule, MatButtonModule, MatSelectModule, MatIconModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MashupMediaMaterialModule} from '../material-module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
	BrowserModule,
	FormsModule,
	HttpModule,
	BrowserAnimationsModule,
	MashupMediaMaterialModule
	/*
	MatInputModule, 
	MatButtonModule,
	MatSelectModule,
	MatIconModule
	*/
	  
  ],
  exports: [],  
  providers: [{ provide: LOCALE_ID, useValue: 'en' }],
  bootstrap: [AppComponent]
})
export class AppModule { }
