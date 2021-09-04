import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IItemReview, ItemReview } from '../item-review.model';
import { ItemReviewService } from '../service/item-review.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

@Component({
  selector: 'jhi-item-review-update',
  templateUrl: './item-review-update.component.html',
})
export class ItemReviewUpdateComponent implements OnInit {
  isSaving = false;

  reviewersCollection: IUserExtra[] = [];
  itemsSharedCollection: IItem[] = [];

  editForm = this.fb.group({
    id: [],
    description: [],
    rating: [],
    reviewDate: [],
    reviewer: [],
    item: [],
  });

  constructor(
    protected itemReviewService: ItemReviewService,
    protected userExtraService: UserExtraService,
    protected itemService: ItemService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemReview }) => {
      this.updateForm(itemReview);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const itemReview = this.createFromForm();
    if (itemReview.id !== undefined) {
      this.subscribeToSaveResponse(this.itemReviewService.update(itemReview));
    } else {
      this.subscribeToSaveResponse(this.itemReviewService.create(itemReview));
    }
  }

  trackUserExtraById(index: number, item: IUserExtra): number {
    return item.id!;
  }

  trackItemById(index: number, item: IItem): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IItemReview>>): void {
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

  protected updateForm(itemReview: IItemReview): void {
    this.editForm.patchValue({
      id: itemReview.id,
      description: itemReview.description,
      rating: itemReview.rating,
      reviewDate: itemReview.reviewDate,
      reviewer: itemReview.reviewer,
      item: itemReview.item,
    });

    this.reviewersCollection = this.userExtraService.addUserExtraToCollectionIfMissing(this.reviewersCollection, itemReview.reviewer);
    this.itemsSharedCollection = this.itemService.addItemToCollectionIfMissing(this.itemsSharedCollection, itemReview.item);
  }

  protected loadRelationshipsOptions(): void {
    this.userExtraService
      .query({ filter: 'itemreview-is-null' })
      .pipe(map((res: HttpResponse<IUserExtra[]>) => res.body ?? []))
      .pipe(
        map((userExtras: IUserExtra[]) =>
          this.userExtraService.addUserExtraToCollectionIfMissing(userExtras, this.editForm.get('reviewer')!.value)
        )
      )
      .subscribe((userExtras: IUserExtra[]) => (this.reviewersCollection = userExtras));

    this.itemService
      .query()
      .pipe(map((res: HttpResponse<IItem[]>) => res.body ?? []))
      .pipe(map((items: IItem[]) => this.itemService.addItemToCollectionIfMissing(items, this.editForm.get('item')!.value)))
      .subscribe((items: IItem[]) => (this.itemsSharedCollection = items));
  }

  protected createFromForm(): IItemReview {
    return {
      ...new ItemReview(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      rating: this.editForm.get(['rating'])!.value,
      reviewDate: this.editForm.get(['reviewDate'])!.value,
      reviewer: this.editForm.get(['reviewer'])!.value,
      item: this.editForm.get(['item'])!.value,
    };
  }
}
