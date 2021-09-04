import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ItemReviewComponent } from '../list/item-review.component';
import { ItemReviewDetailComponent } from '../detail/item-review-detail.component';
import { ItemReviewUpdateComponent } from '../update/item-review-update.component';
import { ItemReviewRoutingResolveService } from './item-review-routing-resolve.service';

const itemReviewRoute: Routes = [
  {
    path: '',
    component: ItemReviewComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ItemReviewDetailComponent,
    resolve: {
      itemReview: ItemReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ItemReviewUpdateComponent,
    resolve: {
      itemReview: ItemReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ItemReviewUpdateComponent,
    resolve: {
      itemReview: ItemReviewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(itemReviewRoute)],
  exports: [RouterModule],
})
export class ItemReviewRoutingModule {}
