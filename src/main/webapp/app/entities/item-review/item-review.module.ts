import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ItemReviewComponent } from './list/item-review.component';
import { ItemReviewDetailComponent } from './detail/item-review-detail.component';
import { ItemReviewUpdateComponent } from './update/item-review-update.component';
import { ItemReviewDeleteDialogComponent } from './delete/item-review-delete-dialog.component';
import { ItemReviewRoutingModule } from './route/item-review-routing.module';

@NgModule({
  imports: [SharedModule, ItemReviewRoutingModule],
  declarations: [ItemReviewComponent, ItemReviewDetailComponent, ItemReviewUpdateComponent, ItemReviewDeleteDialogComponent],
  entryComponents: [ItemReviewDeleteDialogComponent],
})
export class ItemReviewModule {}
