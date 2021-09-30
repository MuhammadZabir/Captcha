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
    this.cartValue.cartBaskets = [];
    this.cart = new BehaviorSubject(this.cartValue);
  }

  addCartBasket(cartBasket: ICartBasket): void {
    this.cartValue.cartBaskets!.push(cartBasket);
    this.cart.next(this.cartValue);
  }

  removeCartBasket(cartBasket: ICartBasket): void {
    this.cartValue.cartBaskets = this.cartValue.cartBaskets!.filter(item => item !== cartBasket);
    this.cart.next(this.cartValue);
  }
}
