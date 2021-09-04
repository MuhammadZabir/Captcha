import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IItemStatus } from '../item-status.model';
import { ItemStatusService } from '../service/item-status.service';

@Component({
  templateUrl: './item-status-delete-dialog.component.html',
})
export class ItemStatusDeleteDialogComponent {
  itemStatus?: IItemStatus;

  constructor(protected itemStatusService: ItemStatusService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.itemStatusService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
