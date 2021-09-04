import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPurchaseHistory } from '../purchase-history.model';
import { PurchaseHistoryService } from '../service/purchase-history.service';
import { PurchaseHistoryDeleteDialogComponent } from '../delete/purchase-history-delete-dialog.component';

@Component({
  selector: 'jhi-purchase-history',
  templateUrl: './purchase-history.component.html',
})
export class PurchaseHistoryComponent implements OnInit {
  purchaseHistories?: IPurchaseHistory[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected purchaseHistoryService: PurchaseHistoryService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.purchaseHistoryService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IPurchaseHistory[]>) => {
            this.isLoading = false;
            this.purchaseHistories = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.purchaseHistoryService.query().subscribe(
      (res: HttpResponse<IPurchaseHistory[]>) => {
        this.isLoading = false;
        this.purchaseHistories = res.body ?? [];
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

  trackId(index: number, item: IPurchaseHistory): number {
    return item.id!;
  }

  delete(purchaseHistory: IPurchaseHistory): void {
    const modalRef = this.modalService.open(PurchaseHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.purchaseHistory = purchaseHistory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
