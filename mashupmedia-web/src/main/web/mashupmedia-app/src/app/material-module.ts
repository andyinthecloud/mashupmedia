import {NgModule} from '@angular/core';
import {
  MatCheckboxModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
  MatSlideToggleModule,
  MatFormFieldModule,
  MatButtonModule,
  MatInputModule,
} from '@angular/material';

@NgModule({
  exports: [
    MatSidenavModule,
    MatCheckboxModule,
    MatIconModule,
    MatToolbarModule,
    MatSlideToggleModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})

export class MaterialModule {}

