import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartSharedService } from 'app/shared/cart/cart-shared.service';

import { IItem } from '../item.model';
import { IImage } from 'app/entities/image/image.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

@Component({
  selector: 'jhi-item-detail',
  templateUrl: './item-detail.component.html',
})
export class ItemDetailComponent implements OnInit {
  item: IItem | null = null;
  imageCollection: IImage[] = [];

  constructor(
    protected activatedRoute: ActivatedRoute,
    private cartSharedService: CartSharedService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ item }) => {
      this.item = item;
      this.imageCollection = item.images!;
    });
  }

  previousState(): void {
    window.history.back();
  }

  addToBasket(): void {
    const cartBasket = <ICartBasket>{};
    cartBasket.amount = 1;
    cartBasket.item = this.item;
    this.cartSharedService.addCartBasket(cartBasket);
    this.previousState();
  }
}
