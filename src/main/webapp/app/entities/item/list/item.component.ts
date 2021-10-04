import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IItem } from '../item.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { ItemService } from '../service/item.service';
import { ImageService } from 'app/entities/image/service/image.service';
import { ItemDeleteDialogComponent } from '../delete/item-delete-dialog.component';

@Component({
  selector: 'jhi-item',
  templateUrl: './item.component.html',
})
export class ItemComponent implements OnInit {
  items?: IItem[] = [];
  currentSearch: string;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  results: any[] = [];

  constructor(
    protected itemService: ItemService,
    protected imageService: ImageService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    if (this.currentSearch) {
      this.itemService
        .search({
          page: pageToLoad - 1,
          query: this.currentSearch,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<IItem[]>) => {
            this.isLoading = false;
            this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          },
          () => {
            this.isLoading = false;
            this.onError();
          }
        );
      return;
    }

    this.itemService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IItem[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          this.onError();
        }
      );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadPage(1);
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(index: number, item: IItem): number {
    return item.id!;
  }

  delete(item: IItem): void {
    const modalRef = this.modalService.open(ItemDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.item = item;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  getImage(): void {
    this.isLoading = true;
    if (this.items) {
      for (const item of this.items) {
        if (item.images) {
          this.imageService.getImage(item.images[0]!.imageDir!).subscribe(result => {
            this.createImageFromBlob(result);
          });
        }
      }
    }
    this.isLoading = false;
  }

  getImageTest(): void {
    this.isLoading = true;
    if (this.items) {
      for (const item of this.items) {
        this.imageService.getImage('/images/banana.jpeg').subscribe(result => {
          this.createImageFromBlob(result);
        });
      }
    }
    this.isLoading = false;
  }

  createImageFromBlob(image: Blob): void {
    const reader = new FileReader();
    reader.addEventListener(
        'load',
        () => {
            this.results.push(reader.result);
        },
        false
    );

    reader.readAsDataURL(image);
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: IItem[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.ngbPaginationPage = this.page;
    if (navigate) {
      this.router.navigate(['/item'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          search: this.currentSearch,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.items = data ?? [];
    this.ngbPaginationPage = this.page;
    this.getImageTest();
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
