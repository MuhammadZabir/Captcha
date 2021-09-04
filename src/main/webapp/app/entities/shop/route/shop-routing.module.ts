import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShopComponent } from '../list/shop.component';
import { ShopDetailComponent } from '../detail/shop-detail.component';
import { ShopUpdateComponent } from '../update/shop-update.component';
import { ShopRoutingResolveService } from './shop-routing-resolve.service';

const shopRoute: Routes = [
  {
    path: '',
    component: ShopComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShopDetailComponent,
    resolve: {
      shop: ShopRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShopUpdateComponent,
    resolve: {
      shop: ShopRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShopUpdateComponent,
    resolve: {
      shop: ShopRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shopRoute)],
  exports: [RouterModule],
})
export class ShopRoutingModule {}
