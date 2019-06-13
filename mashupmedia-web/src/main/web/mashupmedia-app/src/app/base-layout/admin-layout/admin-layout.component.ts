import { Component, OnInit, Input } from '@angular/core';
import { BaseLayoutComponent } from '../base-layout.component';

@Component({
  templateUrl: '../base-layout.component.html',
  styleUrls: ['../base-layout.component.scss']
})
export class AdminLayoutComponent  extends BaseLayoutComponent {


  ngOnInit() {
    this.showMainFooterMenu = true;
    this.showAdminMenu = true;
  }

}
