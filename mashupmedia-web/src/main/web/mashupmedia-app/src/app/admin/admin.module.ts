import { NgModule }       from '@angular/core';
import { CommonModule }   from '@angular/common';

import { AdminComponent }           from './admin/admin.component';
import { AdminDashboardComponent }  from './admin-dashboard/admin-dashboard.component';
import { ManageCrisesComponent }    from './manage-crises/manage-crises.component';
import { ManageHeroesComponent }    from './manage-heroes/manage-heroes.component';

import { AdminRoutingModule }       from './admin-routing.module';
import { NetworkComponent } from './network/network.component';
import { BaseComponent } from '../base/base.component';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material-module';



@NgModule({
  imports: [
    CommonModule,
    AdminRoutingModule,
    MaterialModule
  ],
  declarations: [
    AdminComponent,
    AdminDashboardComponent,
    ManageCrisesComponent,
    ManageHeroesComponent,
    NetworkComponent,
    BaseComponent

  ]
})
export class AdminModule {}
