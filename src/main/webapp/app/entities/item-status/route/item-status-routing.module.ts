import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ItemStatusComponent } from '../list/item-status.component';
import { ItemStatusDetailComponent } from '../detail/item-status-detail.component';
import { ItemStatusUpdateComponent } from '../update/item-status-update.component';
import { ItemStatusRoutingResolveService } from './item-status-routing-resolve.service';

const itemStatusRoute: Routes = [
  {
    path: '',
    component: ItemStatusComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ItemStatusDetailComponent,
    resolve: {
      itemStatus: ItemStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ItemStatusUpdateComponent,
    resolve: {
      itemStatus: ItemStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ItemStatusUpdateComponent,
    resolve: {
      itemStatus: ItemStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(itemStatusRoute)],
  exports: [RouterModule],
})
export class ItemStatusRoutingModule {}
