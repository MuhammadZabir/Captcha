import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IItemReview } from '../item-review.model';
import { ItemReviewService } from '../service/item-review.service';
import { ItemReviewDeleteDialogComponent } from '../delete/item-review-delete-dialog.component';

@Component({
  selector: 'jhi-item-review',
  templateUrl: './item-review.component.html',
})
export class ItemReviewComponent implements OnInit {
  itemReviews?: IItemReview[];
  isLoading = false;
  currentSearch: string;

  constructor(protected itemReviewService: ItemReviewService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.itemReviewService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IItemReview[]>) => {
            this.isLoading = false;
            this.itemReviews = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.itemReviewService.query().subscribe(
      (res: HttpResponse<IItemReview[]>) => {
        this.isLoading = false;
        this.itemReviews = res.body ?? [];
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

  trackId(index: number, item: IItemReview): number {
    return item.id!;
  }

  delete(itemReview: IItemReview): void {
    const modalRef = this.modalService.open(ItemReviewDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.itemReview = itemReview;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
