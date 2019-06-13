import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { AdminLayoutComponent } from 'src/app/base-layout/admin-layout/admin-layout.component';

@Component({
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.scss']
})
export class NetworkComponent extends AdminLayoutComponent {
  proxyFormGroup: FormGroup;
  constructor(formBuilder: FormBuilder) {
    super();
  }

  ngOnInit() {
    console.log('ngOnInit');
    this.showAdminMenu = false;
    this.proxyFormGroup = new FormGroup({
      enableHttpProxy: new FormControl(false),
      host: new FormControl(),
      port: new FormControl(),
      username: new FormControl(),
      password: new FormControl()
    });
  }

  onSubmit(form: FormGroup) {
    // form.value.
  }

}
