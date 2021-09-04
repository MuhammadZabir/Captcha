import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICartBasket } from '../cart-basket.model';
import { CartBasketService } from '../service/cart-basket.service';
import { CartBasketDeleteDialogComponent } from '../delete/cart-basket-delete-dialog.component';

@Component({
  selector: 'jhi-cart-basket',
  templateUrl: './cart-basket.component.html',
})
export class CartBasketComponent implements OnInit {
  cartBaskets?: ICartBasket[];
  isLoading = false;
  currentSearch: string;

  constructor(protected cartBasketService: CartBasketService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cartBasketService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<ICartBasket[]>) => {
            this.isLoading = false;
            this.cartBaskets = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.cartBasketService.query().subscribe(
      (res: HttpResponse<ICartBasket[]>) => {
        this.isLoading = false;
        this.cartBaskets = res.body ?? [];
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

  trackId(index: number, item: ICartBasket): number {
    return item.id!;
  }

  delete(cartBasket: ICartBasket): void {
    const modalRef = this.modalService.open(CartBasketDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cartBasket = cartBasket;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
