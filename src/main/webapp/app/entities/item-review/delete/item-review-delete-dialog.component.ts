import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IItemReview } from '../item-review.model';
import { ItemReviewService } from '../service/item-review.service';

@Component({
  templateUrl: './item-review-delete-dialog.component.html',
})
export class ItemReviewDeleteDialogComponent {
  itemReview?: IItemReview;

  constructor(protected itemReviewService: ItemReviewService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.itemReviewService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
