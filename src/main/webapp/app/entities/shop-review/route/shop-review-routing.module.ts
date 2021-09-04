import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShopReviewComponent } from '../list/shop-review.component';
import { ShopReviewDetailComponent } from '../detail/shop-review-detail.component';
import { ShopReviewUpdateComponent } from '../update/shop-review-update.component';
import { ShopReviewRoutingResolveService } from './shop-review-routing-resolve.service';

const shopReviewRoute: Routes = [
  {
    path: '',
    component: ShopReviewComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShopReviewDetailComponent,
    resolve: {
      shopReview: ShopReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShopReviewUpdateComponent,
    resolve: {
      shopReview: ShopReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShopReviewUpdateComponent,
    resolve: {
      shopReview: ShopReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shopReviewRoute)],
  exports: [RouterModule],
})
export class ShopReviewRoutingModule {}
