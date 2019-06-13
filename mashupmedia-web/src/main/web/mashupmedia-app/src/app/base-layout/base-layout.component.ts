import { Component, OnInit, Input } from '@angular/core';

@Component({
  templateUrl: './base-layout.component.html',
  styleUrls: ['./base-layout.component.scss']
})
export class BaseLayoutComponent implements OnInit {

  opened: boolean;
  showAdminMenu: boolean;
  showMainFooterMenu: boolean;

  constructor() {

  }

  ngOnInit() {
  }

}
