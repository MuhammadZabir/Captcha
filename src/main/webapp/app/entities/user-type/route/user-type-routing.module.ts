import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserTypeComponent } from '../list/user-type.component';
import { UserTypeDetailComponent } from '../detail/user-type-detail.component';
import { UserTypeUpdateComponent } from '../update/user-type-update.component';
import { UserTypeRoutingResolveService } from './user-type-routing-resolve.service';

const userTypeRoute: Routes = [
  {
    path: '',
    component: UserTypeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserTypeDetailComponent,
    resolve: {
      userType: UserTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserTypeUpdateComponent,
    resolve: {
      userType: UserTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserTypeUpdateComponent,
    resolve: {
      userType: UserTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userTypeRoute)],
  exports: [RouterModule],
})
export class UserTypeRoutingModule {}
