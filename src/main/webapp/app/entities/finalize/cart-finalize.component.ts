import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { finalize, map, mergeMap } from 'rxjs/operators';

import { ICart, Cart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { CartBasketService } from 'app/entities/cart-basket/service/cart-basket.service';
import { CartSharedService } from 'app/shared/cart/cart-shared.service';

@Component({
  selector: 'jhi-cart-finalize',
  templateUrl: './cart-finalize.component.html',
})
export class CartFinalizeComponent implements OnInit {
  isSaving = false;

  totalPrice = 0;
  buyersCollection: IUserExtra[] = [];
  cartBasketsCollection: ICartBasket[] = [];
  imagesCollection: any[] = [];
  cart: ICart = {};
  isAllow = false;

  constructor(
    protected cartService: CartService,
    protected cartBasketService: CartBasketService,
    protected userExtraService: UserExtraService,
    protected cartSharedService: CartSharedService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.cartSharedService.cart.subscribe((cart: ICart) => {
      this.updateForm(cart);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.subscribeToSaveResponse(this.cartService.create(this.cart));
  }

  trackUserExtraById(index: number, item: IUserExtra): number {
    return item.id!;
  }

  validate(event: Event): void {
    const captchaValue = (event.target as HTMLInputElement).value;
    if (captchaValue === this.cart.hiddenCaptcha) {
      this.isAllow = true;
    } else {
      this.isAllow = false;
    }
    this.cart.captcha = captchaValue;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICart>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.cartSharedService.renewCartBasket();
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cart: ICart): void {
    this.cartService.getCaptcha(cart).pipe(mergeMap((httpCart: HttpResponse<ICart>) => of(httpCart.body))).subscribe(
      (cartValue: ICart | null) => {
        this.cart = cartValue!;
        if (this.cart.cartBaskets) {
          this.cartBasketsCollection = this.cart.cartBaskets;
          this.calculatingTotalPrice();
          this.cart.totalPrice = this.totalPrice;
        }
      });

    this.cartSharedService.images.subscribe((imageCollection) => {
      this.imagesCollection = imageCollection;
    });
    this.buyersCollection = this.userExtraService.addUserExtraToCollectionIfMissing(this.buyersCollection, cart.buyer);
  }

  protected calculatingTotalPrice(): void {
    this.totalPrice = 0;
    for (const cartBasket of this.cartBasketsCollection) {
      let price = 0;
      if (cartBasket.item?.price) {
        price = cartBasket.item.price;
      }
      this.totalPrice = this.totalPrice + price;
    }
  }
}
