import {NgModule} from '@angular/core';
import {
  MatCheckboxModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
  MatSlideToggleModule,
  MatFormFieldModule,
} from '@angular/material';

@NgModule({
  exports: [
    MatSidenavModule,
    MatCheckboxModule,
    MatIconModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatFormFieldModule
  ]
})

export class MaterialModule {}

