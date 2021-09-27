import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ICart } from 'app/entities/cart/cart.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

@Injectable({
  providedIn: 'root',
})
export class CartSharedService {
  cartValue = <ICart>{};
  cart: BehaviorSubject<ICart>;

  constructor() {
    this.cart = new BehaviorSubject(this.cartValue);
  }

  addCartBasket(cartBasket: ICartBasket): void {
    this.cart.subscribe((c) => {
      c.cartBaskets!.push(cartBasket);
      this.cart.next(c);
    });
  }

  removeCartBasket(cartBasket: ICartBasket): void {
    this.cart.subscribe((c) => {
      c.cartBaskets = c.cartBaskets!.filter(item => item !== cartBasket);
      this.cart.next(c);
    });
  }
}
