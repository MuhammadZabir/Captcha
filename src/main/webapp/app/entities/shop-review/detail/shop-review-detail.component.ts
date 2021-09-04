import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShopReview } from '../shop-review.model';

@Component({
  selector: 'jhi-shop-review-detail',
  templateUrl: './shop-review-detail.component.html',
})
export class ShopReviewDetailComponent implements OnInit {
  shopReview: IShopReview | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shopReview }) => {
      this.shopReview = shopReview;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
