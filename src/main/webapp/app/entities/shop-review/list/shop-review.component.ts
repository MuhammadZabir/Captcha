import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IShopReview } from '../shop-review.model';
import { ShopReviewService } from '../service/shop-review.service';
import { ShopReviewDeleteDialogComponent } from '../delete/shop-review-delete-dialog.component';

@Component({
  selector: 'jhi-shop-review',
  templateUrl: './shop-review.component.html',
})
export class ShopReviewComponent implements OnInit {
  shopReviews?: IShopReview[];
  isLoading = false;
  currentSearch: string;

  constructor(protected shopReviewService: ShopReviewService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.shopReviewService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IShopReview[]>) => {
            this.isLoading = false;
            this.shopReviews = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.shopReviewService.query().subscribe(
      (res: HttpResponse<IShopReview[]>) => {
        this.isLoading = false;
        this.shopReviews = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IShopReview): number {
    return item.id!;
  }

  delete(shopReview: IShopReview): void {
    const modalRef = this.modalService.open(ShopReviewDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.shopReview = shopReview;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
