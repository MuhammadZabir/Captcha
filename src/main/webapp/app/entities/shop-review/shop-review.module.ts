import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShopReviewComponent } from './list/shop-review.component';
import { ShopReviewDetailComponent } from './detail/shop-review-detail.component';
import { ShopReviewUpdateComponent } from './update/shop-review-update.component';
import { ShopReviewDeleteDialogComponent } from './delete/shop-review-delete-dialog.component';
import { ShopReviewRoutingModule } from './route/shop-review-routing.module';

@NgModule({
  imports: [SharedModule, ShopReviewRoutingModule],
  declarations: [ShopReviewComponent, ShopReviewDetailComponent, ShopReviewUpdateComponent, ShopReviewDeleteDialogComponent],
  entryComponents: [ShopReviewDeleteDialogComponent],
})
export class ShopReviewModule {}
