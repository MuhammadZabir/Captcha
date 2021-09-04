import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICartBasket } from '../cart-basket.model';

@Component({
  selector: 'jhi-cart-basket-detail',
  templateUrl: './cart-basket-detail.component.html',
})
export class CartBasketDetailComponent implements OnInit {
  cartBasket: ICartBasket | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartBasket }) => {
      this.cartBasket = cartBasket;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
