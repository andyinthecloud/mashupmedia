import {NgModule} from '@angular/core';
import {
  MatCheckboxModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
} from '@angular/material';

@NgModule({
  exports: [
    MatSidenavModule,
    MatCheckboxModule,
    MatIconModule,
    MatToolbarModule
  ]
})

export class MaterialModule {}

