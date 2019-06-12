import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminComponent }           from './admin/admin.component';
import { AdminDashboardComponent }  from './admin-dashboard/admin-dashboard.component';
import { ManageCrisesComponent }    from './manage-crises/manage-crises.component';
import { ManageHeroesComponent }    from './manage-heroes/manage-heroes.component';

import { AuthGuard }                from '../auth/auth.guard';
import { NetworkComponent } from './network/network.component';
import { BaseComponent } from '../base/base.component';

const adminRoutes: Routes = [
  /*
  {
    path: '',
    component: AdminComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        canActivateChild: [AuthGuard],
        children: [
          // { path: 'crises', component: ManageCrisesComponent },
          // { path: 'heroes', component: ManageHeroesComponent },
          { path: 'network', component: NetworkComponent },
//          { path: '', component: AdminDashboardComponent }
        ]
      }
    ]
  }
  */

  {
    path: '',
    component: BaseComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'network', component: NetworkComponent },
    ]
  }

];

@NgModule({
  imports: [
    RouterModule.forChild(adminRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AdminRoutingModule {}
