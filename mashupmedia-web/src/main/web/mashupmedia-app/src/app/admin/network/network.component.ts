import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  // selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.scss']
})
export class NetworkComponent implements OnInit {
  formGroup: FormGroup;

  enableHttpProxy = 'accent';
  checked = false;

  url = new FormControl('');

  // disabled = false;
  // enableHttpProxy = new FormControl('');

  // activateHttpProxy = new FormControl('');
  // url = new FormControl('');
  // port = new FormControl('');
  // username = new FormControl('');
  // password = new FormControl('');

constructor(formBuilder: FormBuilder) {
  this.formGroup = formBuilder.group({
    enableHttpProxy: true,
    url: ''

  })
}

  ngOnInit() {
  }

}
