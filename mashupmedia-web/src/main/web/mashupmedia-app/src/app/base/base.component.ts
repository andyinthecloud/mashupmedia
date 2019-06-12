import { Component, OnInit } from '@angular/core';

@Component({
  // selector: 'app-base',
  selector: 'main-content',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.scss']
})
export class BaseComponent implements OnInit {

  opened: boolean;

  constructor() { }

  ngOnInit() {
  }

}
