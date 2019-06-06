import { Component, ViewEncapsulation, TemplateRef, ViewChild, OnInit } from '@angular/core';
import { AppComponent } from '../../app.component';
import { AuthService } from '../../auth/auth.service';

@Component({

  // selector: 'app-content',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']

/*
  templateUrl: '../../mashup-media.template.html',
  styleUrls: ['../../mashup-media.template.scss']
*/

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
