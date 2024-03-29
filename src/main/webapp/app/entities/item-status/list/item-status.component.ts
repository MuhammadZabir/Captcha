import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IItemStatus } from '../item-status.model';
import { ItemStatusService } from '../service/item-status.service';
import { ItemStatusDeleteDialogComponent } from '../delete/item-status-delete-dialog.component';

@Component({
  selector: 'jhi-item-status',
  templateUrl: './item-status.component.html',
})
export class ItemStatusComponent implements OnInit {
  itemStatuses?: IItemStatus[];
  isLoading = false;
  currentSearch: string;

  constructor(protected itemStatusService: ItemStatusService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.itemStatusService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IItemStatus[]>) => {
            this.isLoading = false;
            this.itemStatuses = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.itemStatusService.query().subscribe(
      (res: HttpResponse<IItemStatus[]>) => {
        this.isLoading = false;
        this.itemStatuses = res.body ?? [];
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

  trackId(index: number, item: IItemStatus): number {
    return item.id!;
  }

  delete(itemStatus: IItemStatus): void {
    const modalRef = this.modalService.open(ItemStatusDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.itemStatus = itemStatus;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
