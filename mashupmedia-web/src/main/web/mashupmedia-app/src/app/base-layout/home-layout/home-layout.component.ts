import { Component, OnInit } from '@angular/core';
import { BaseLayoutComponent } from '../base-layout.component';

@Component({
  templateUrl: '../base-layout.component.html',
  styleUrls: ['../base-layout.component.scss'],
  providers: [HomeLayoutComponent]
})
export class HomeLayoutComponent extends BaseLayoutComponent {

  ngOnInit() {
    this.showMainFooterMenu = true;
  }

}
