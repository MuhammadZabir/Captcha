import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICartBasket, CartBasket } from '../cart-basket.model';
import { CartBasketService } from '../service/cart-basket.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

@Component({
  selector: 'jhi-cart-basket-update',
  templateUrl: './cart-basket-update.component.html',
})
export class CartBasketUpdateComponent implements OnInit {
  isSaving = false;

  cartsSharedCollection: ICart[] = [];

  editForm = this.fb.group({
    id: [],
    amount: [],
    cart: [],
  });

  constructor(
    protected cartBasketService: CartBasketService,
    protected cartService: CartService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cartBasket }) => {
      this.updateForm(cartBasket);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cartBasket = this.createFromForm();
    if (cartBasket.id !== undefined) {
      this.subscribeToSaveResponse(this.cartBasketService.update(cartBasket));
    } else {
      this.subscribeToSaveResponse(this.cartBasketService.create(cartBasket));
    }
  }

  trackCartById(index: number, item: ICart): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICartBasket>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cartBasket: ICartBasket): void {
    this.editForm.patchValue({
      id: cartBasket.id,
      amount: cartBasket.amount,
      cart: cartBasket.cart,
    });

    this.cartsSharedCollection = this.cartService.addCartToCollectionIfMissing(this.cartsSharedCollection, cartBasket.cart);
  }

  protected loadRelationshipsOptions(): void {
    this.cartService
      .query()
      .pipe(map((res: HttpResponse<ICart[]>) => res.body ?? []))
      .pipe(map((carts: ICart[]) => this.cartService.addCartToCollectionIfMissing(carts, this.editForm.get('cart')!.value)))
      .subscribe((carts: ICart[]) => (this.cartsSharedCollection = carts));
  }

  protected createFromForm(): ICartBasket {
    return {
      ...new CartBasket(),
      id: this.editForm.get(['id'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      cart: this.editForm.get(['cart'])!.value,
    };
  }
}
