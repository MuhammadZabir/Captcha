import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IShopReview } from '../shop-review.model';
import { ShopReviewService } from '../service/shop-review.service';

@Component({
  templateUrl: './shop-review-delete-dialog.component.html',
})
export class ShopReviewDeleteDialogComponent {
  shopReview?: IShopReview;

  constructor(protected shopReviewService: ShopReviewService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.shopReviewService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
