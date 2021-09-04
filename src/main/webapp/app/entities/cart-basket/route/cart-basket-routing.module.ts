import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CartBasketComponent } from '../list/cart-basket.component';
import { CartBasketDetailComponent } from '../detail/cart-basket-detail.component';
import { CartBasketUpdateComponent } from '../update/cart-basket-update.component';
import { CartBasketRoutingResolveService } from './cart-basket-routing-resolve.service';

const cartBasketRoute: Routes = [
  {
    path: '',
    component: CartBasketComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CartBasketDetailComponent,
    resolve: {
      cartBasket: CartBasketRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CartBasketUpdateComponent,
    resolve: {
      cartBasket: CartBasketRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CartBasketUpdateComponent,
    resolve: {
      cartBasket: CartBasketRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cartBasketRoute)],
  exports: [RouterModule],
})
export class CartBasketRoutingModule {}
