import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShop } from '../shop.model';

@Component({
  selector: 'jhi-shop-detail',
  templateUrl: './shop-detail.component.html',
})
export class ShopDetailComponent implements OnInit {
  shop: IShop | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shop }) => {
      this.shop = shop;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
