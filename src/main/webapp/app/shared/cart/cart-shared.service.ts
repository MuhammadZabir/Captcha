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
  imagesValue: any[] = [];
  images: BehaviorSubject<any[]>;

  constructor() {
    this.cartValue.cartBaskets = [];
    this.cart = new BehaviorSubject(this.cartValue);
    this.images = new BehaviorSubject(this.imagesValue);
  }

  addCartBasket(cartBasket: ICartBasket, image: any): void {
    this.cartValue.cartBaskets!.push(cartBasket);
    this.imagesValue.push(image);
    this.cart.next(this.cartValue);
    this.images.next(this.imagesValue);
  }

  removeCartBasket(index: number): void {
    this.cartValue.cartBaskets!.splice(index, 1);
    this.imagesValue.splice(index, 1);
    this.cart.next(this.cartValue);
    this.images.next(this.imagesValue);
  }

  renewCartBasket(): void {
    this.cartValue = <ICart>{};
    this.cartValue.cartBaskets = [];
    this.imagesValue = [];
    this.cart.next(this.cartValue);
    this.images.next(this.imagesValue);
  }
}
