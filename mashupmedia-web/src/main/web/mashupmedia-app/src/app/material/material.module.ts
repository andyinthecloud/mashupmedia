import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule, MatCheckboxModule} from '@angular/material';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatButtonModule,
    BrowserAnimationsModule,
    MatCheckboxModule
  ],
  exports: [
	  MatButtonModule, 
	  MatCheckboxModule,
	  BrowserAnimationsModule
  ]
})
export class MaterialModule { }
