import { NgModule }       from '@angular/core';
import { CommonModule }   from '@angular/common';

import { AdminComponent }           from './admin/admin.component';
import { AdminDashboardComponent }  from './admin-dashboard/admin-dashboard.component';
import { ManageCrisesComponent }    from './manage-crises/manage-crises.component';
import { ManageHeroesComponent }    from './manage-heroes/manage-heroes.component';

import { AdminRoutingModule }       from './admin-routing.module';
import { NetworkComponent } from './network/network.component';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material-module';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminLayoutComponent } from '../base-layout/admin-layout/admin-layout.component';
// import { AdminLayoutComponent } from '../base-layout/admin-layout/admin-layout.component';



@NgModule({
  imports: [
    CommonModule,
    AdminRoutingModule,
    MaterialModule,
    ReactiveFormsModule
  ],
  declarations: [
    AdminComponent,
    AdminDashboardComponent,
    ManageCrisesComponent,
    ManageHeroesComponent,
    NetworkComponent,
    AdminLayoutComponent

  ]
})
export class AdminModule {}
