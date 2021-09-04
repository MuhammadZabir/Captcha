import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IItem } from '../item.model';
import { ItemService } from '../service/item.service';

@Component({
  templateUrl: './item-delete-dialog.component.html',
})
export class ItemDeleteDialogComponent {
  item?: IItem;

  constructor(protected itemService: ItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.itemService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
