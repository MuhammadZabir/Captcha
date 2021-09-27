import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ICart } from 'app/entities/cart/cart.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

@Injectable({
  providedIn: 'root',
})
export class CartSharedService {
  cartValue: ICart = [];
  cart: BehaviorSubject<Cart>;

  constructor() {
    this.cart = new BehaviorSubject(this.cartValue);
  }

  addCartBasket(cartBasket: ICartBasket) {
    this.count.next(this.cartValue.cartBasket.push(cartBasket);
  }

  removeCartBasket(cartBasket: ICartBasket) {
    this.count.next(this.cartValue.cartBasket = this.cartValue.cartBasket.filter(item => item !== cartBasket));
  }
}
