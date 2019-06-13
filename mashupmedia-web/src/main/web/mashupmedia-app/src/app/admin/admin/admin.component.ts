import { Component, ViewEncapsulation, TemplateRef, ViewChild, OnInit } from '@angular/core';
import { AppComponent } from '../../app.component';
import { AuthService } from '../../auth/auth.service';
import { BaseLayoutComponent } from 'src/app/base-layout/base-layout.component';

@Component({
  // selector: 'main-content',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']

})
export class AdminComponent  implements OnInit {

  /*
  @ViewChild('showSecureContent')
  showSecureContent: TemplateRef<any>;
*/
// showSecureContent: string = true;

getShowSecureContent() {
  return true;
}



//   constructor(public authService: AuthService) {
// super(authService);
// this.showSecureContent = true;
//   }


  // getShowSecureContent() {
  //   this.showSecureContent;
  // }


  ngOnInit() {
    // console.log(this.showSecureContent);

    console.log(this.getShowSecureContent());
}
}
