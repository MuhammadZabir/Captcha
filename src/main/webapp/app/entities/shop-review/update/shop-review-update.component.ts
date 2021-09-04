import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IShopReview, ShopReview } from '../shop-review.model';
import { ShopReviewService } from '../service/shop-review.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { IShop } from 'app/entities/shop/shop.model';
import { ShopService } from 'app/entities/shop/service/shop.service';

@Component({
  selector: 'jhi-shop-review-update',
  templateUrl: './shop-review-update.component.html',
})
export class ShopReviewUpdateComponent implements OnInit {
  isSaving = false;

  reviewersCollection: IUserExtra[] = [];
  shopsSharedCollection: IShop[] = [];

  editForm = this.fb.group({
    id: [],
    description: [],
    rating: [],
    reviewDate: [],
    reviewer: [],
    shop: [],
  });

  constructor(
    protected shopReviewService: ShopReviewService,
    protected userExtraService: UserExtraService,
    protected shopService: ShopService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shopReview }) => {
      this.updateForm(shopReview);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shopReview = this.createFromForm();
    if (shopReview.id !== undefined) {
      this.subscribeToSaveResponse(this.shopReviewService.update(shopReview));
    } else {
      this.subscribeToSaveResponse(this.shopReviewService.create(shopReview));
    }
  }

  trackUserExtraById(index: number, item: IUserExtra): number {
    return item.id!;
  }

  trackShopById(index: number, item: IShop): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShopReview>>): void {
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

  protected updateForm(shopReview: IShopReview): void {
    this.editForm.patchValue({
      id: shopReview.id,
      description: shopReview.description,
      rating: shopReview.rating,
      reviewDate: shopReview.reviewDate,
      reviewer: shopReview.reviewer,
      shop: shopReview.shop,
    });

    this.reviewersCollection = this.userExtraService.addUserExtraToCollectionIfMissing(this.reviewersCollection, shopReview.reviewer);
    this.shopsSharedCollection = this.shopService.addShopToCollectionIfMissing(this.shopsSharedCollection, shopReview.shop);
  }

  protected loadRelationshipsOptions(): void {
    this.userExtraService
      .query({ filter: 'shopreview-is-null' })
      .pipe(map((res: HttpResponse<IUserExtra[]>) => res.body ?? []))
      .pipe(
        map((userExtras: IUserExtra[]) =>
          this.userExtraService.addUserExtraToCollectionIfMissing(userExtras, this.editForm.get('reviewer')!.value)
        )
      )
      .subscribe((userExtras: IUserExtra[]) => (this.reviewersCollection = userExtras));

    this.shopService
      .query()
      .pipe(map((res: HttpResponse<IShop[]>) => res.body ?? []))
      .pipe(map((shops: IShop[]) => this.shopService.addShopToCollectionIfMissing(shops, this.editForm.get('shop')!.value)))
      .subscribe((shops: IShop[]) => (this.shopsSharedCollection = shops));
  }

  protected createFromForm(): IShopReview {
    return {
      ...new ShopReview(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      rating: this.editForm.get(['rating'])!.value,
      reviewDate: this.editForm.get(['reviewDate'])!.value,
      reviewer: this.editForm.get(['reviewer'])!.value,
      shop: this.editForm.get(['shop'])!.value,
    };
  }
}
