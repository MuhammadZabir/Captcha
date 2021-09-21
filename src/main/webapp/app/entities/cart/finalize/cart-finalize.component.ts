import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICart, Cart } from '../cart.model';
import { CartService } from '../service/cart.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { CartBasketService } from 'app/entities/cart-basket/service/cart-basket.service';

@Component({
  selector: 'jhi-cart-finalize',
  templateUrl: './cart-finalize.component.html',
})
export class CartFinalizeComponent implements OnInit {
  isSaving = false;

  buyersCollection: IUserExtra[] = [];
  cartBasketsCollection: ICartBasket[] = [];

  editForm = this.fb.group({
    id: [],
    totalPrice: [],
    captcha: [],
    hiddenCaptcha: [],
    realCaptcha: [],

    buyer: [],
  });

  constructor(
    protected cartService: CartService,
    protected cartBasketService: CartBasketService,
    protected userExtraService: UserExtraService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cart }) => {
      this.updateForm(cart);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cart = this.createFromForm();
    if (cart.id !== undefined) {
      this.subscribeToSaveResponse(this.cartService.update(cart));
    } else {
      this.subscribeToSaveResponse(this.cartService.create(cart));
    }
  }

  trackUserExtraById(index: number, item: IUserExtra): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICart>>): void {
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

  protected updateForm(cart: ICart): void {
    this.cartService.getCaptcha(cart).pipe(finalize(() => this.onSaveFinalize())).subscribe();
    this.editForm.patchValue({
      id: cart.id,
      totalPrice: cart.totalPrice,
      hiddenCaptcha: cart.hiddenCaptcha,
      realCaptcha: cart.realCaptcha,
      buyer: cart.buyer,
    });

    this.buyersCollection = this.userExtraService.addUserExtraToCollectionIfMissing(this.buyersCollection, cart.buyer);
    this.cartBasketsCollection = this.cartBasketService.findByCartId(cart.id);
  }

  protected loadRelationshipsOptions(id: number): void {
    this.userExtraService
      .query({ filter: 'cart-is-null' })
      .pipe(map((res: HttpResponse<IUserExtra[]>) => res.body ?? []))
      .pipe(
        map((userExtras: IUserExtra[]) =>
          this.userExtraService.addUserExtraToCollectionIfMissing(userExtras, this.editForm.get('buyer')!.value)
        )
      )
      .subscribe((userExtras: IUserExtra[]) => (this.buyersCollection = userExtras));

    this.cartBasketService.findByCartId(id).pipe()
  }

  protected createFromForm(): ICart {
    return {
      ...new Cart(),
      id: this.editForm.get(['id'])!.value,
      totalPrice: this.editForm.get(['totalPrice'])!.value,
      buyer: this.editForm.get(['buyer'])!.value,
    };
  }
}
