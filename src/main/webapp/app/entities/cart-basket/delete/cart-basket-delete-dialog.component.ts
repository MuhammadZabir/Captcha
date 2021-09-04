import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartBasket } from '../cart-basket.model';
import { CartBasketService } from '../service/cart-basket.service';

@Component({
  templateUrl: './cart-basket-delete-dialog.component.html',
})
export class CartBasketDeleteDialogComponent {
  cartBasket?: ICartBasket;

  constructor(protected cartBasketService: CartBasketService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartBasketService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
