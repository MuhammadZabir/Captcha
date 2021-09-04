import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPurchaseHistory, PurchaseHistory } from '../purchase-history.model';
import { PurchaseHistoryService } from '../service/purchase-history.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';

@Component({
  selector: 'jhi-purchase-history-update',
  templateUrl: './purchase-history-update.component.html',
})
export class PurchaseHistoryUpdateComponent implements OnInit {
  isSaving = false;

  cartsCollection: ICart[] = [];
  userExtrasSharedCollection: IUserExtra[] = [];

  editForm = this.fb.group({
    id: [],
    purchaseDate: [],
    shippingDate: [],
    billingAddress: [],
    paymentStatus: [],
    cart: [],
    buyer: [],
  });

  constructor(
    protected purchaseHistoryService: PurchaseHistoryService,
    protected cartService: CartService,
    protected userExtraService: UserExtraService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseHistory }) => {
      this.updateForm(purchaseHistory);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchaseHistory = this.createFromForm();
    if (purchaseHistory.id !== undefined) {
      this.subscribeToSaveResponse(this.purchaseHistoryService.update(purchaseHistory));
    } else {
      this.subscribeToSaveResponse(this.purchaseHistoryService.create(purchaseHistory));
    }
  }

  trackCartById(index: number, item: ICart): number {
    return item.id!;
  }

  trackUserExtraById(index: number, item: IUserExtra): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchaseHistory>>): void {
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

  protected updateForm(purchaseHistory: IPurchaseHistory): void {
    this.editForm.patchValue({
      id: purchaseHistory.id,
      purchaseDate: purchaseHistory.purchaseDate,
      shippingDate: purchaseHistory.shippingDate,
      billingAddress: purchaseHistory.billingAddress,
      paymentStatus: purchaseHistory.paymentStatus,
      cart: purchaseHistory.cart,
      buyer: purchaseHistory.buyer,
    });

    this.cartsCollection = this.cartService.addCartToCollectionIfMissing(this.cartsCollection, purchaseHistory.cart);
    this.userExtrasSharedCollection = this.userExtraService.addUserExtraToCollectionIfMissing(
      this.userExtrasSharedCollection,
      purchaseHistory.buyer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cartService
      .query({ filter: 'purchasehistory-is-null' })
      .pipe(map((res: HttpResponse<ICart[]>) => res.body ?? []))
      .pipe(map((carts: ICart[]) => this.cartService.addCartToCollectionIfMissing(carts, this.editForm.get('cart')!.value)))
      .subscribe((carts: ICart[]) => (this.cartsCollection = carts));

    this.userExtraService
      .query()
      .pipe(map((res: HttpResponse<IUserExtra[]>) => res.body ?? []))
      .pipe(
        map((userExtras: IUserExtra[]) =>
          this.userExtraService.addUserExtraToCollectionIfMissing(userExtras, this.editForm.get('buyer')!.value)
        )
      )
      .subscribe((userExtras: IUserExtra[]) => (this.userExtrasSharedCollection = userExtras));
  }

  protected createFromForm(): IPurchaseHistory {
    return {
      ...new PurchaseHistory(),
      id: this.editForm.get(['id'])!.value,
      purchaseDate: this.editForm.get(['purchaseDate'])!.value,
      shippingDate: this.editForm.get(['shippingDate'])!.value,
      billingAddress: this.editForm.get(['billingAddress'])!.value,
      paymentStatus: this.editForm.get(['paymentStatus'])!.value,
      cart: this.editForm.get(['cart'])!.value,
      buyer: this.editForm.get(['buyer'])!.value,
    };
  }
}
