import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IItemReview } from '../item-review.model';

@Component({
  selector: 'jhi-item-review-detail',
  templateUrl: './item-review-detail.component.html',
})
export class ItemReviewDetailComponent implements OnInit {
  itemReview: IItemReview | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemReview }) => {
      this.itemReview = itemReview;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
