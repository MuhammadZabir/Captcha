import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PurchaseHistoryComponent } from '../list/purchase-history.component';
import { PurchaseHistoryDetailComponent } from '../detail/purchase-history-detail.component';
import { PurchaseHistoryUpdateComponent } from '../update/purchase-history-update.component';
import { PurchaseHistoryRoutingResolveService } from './purchase-history-routing-resolve.service';

const purchaseHistoryRoute: Routes = [
  {
    path: '',
    component: PurchaseHistoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PurchaseHistoryDetailComponent,
    resolve: {
      purchaseHistory: PurchaseHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PurchaseHistoryUpdateComponent,
    resolve: {
      purchaseHistory: PurchaseHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PurchaseHistoryUpdateComponent,
    resolve: {
      purchaseHistory: PurchaseHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(purchaseHistoryRoute)],
  exports: [RouterModule],
})
export class PurchaseHistoryRoutingModule {}
