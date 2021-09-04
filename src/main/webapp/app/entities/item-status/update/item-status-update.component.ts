import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IItemStatus, ItemStatus } from '../item-status.model';
import { ItemStatusService } from '../service/item-status.service';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

@Component({
  selector: 'jhi-item-status-update',
  templateUrl: './item-status-update.component.html',
})
export class ItemStatusUpdateComponent implements OnInit {
  isSaving = false;

  itemsCollection: IItem[] = [];

  editForm = this.fb.group({
    id: [],
    amountAvailable: [],
    amountSold: [],
    availabilityStatus: [],
    item: [],
  });

  constructor(
    protected itemStatusService: ItemStatusService,
    protected itemService: ItemService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemStatus }) => {
      this.updateForm(itemStatus);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const itemStatus = this.createFromForm();
    if (itemStatus.id !== undefined) {
      this.subscribeToSaveResponse(this.itemStatusService.update(itemStatus));
    } else {
      this.subscribeToSaveResponse(this.itemStatusService.create(itemStatus));
    }
  }

  trackItemById(index: number, item: IItem): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IItemStatus>>): void {
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

  protected updateForm(itemStatus: IItemStatus): void {
    this.editForm.patchValue({
      id: itemStatus.id,
      amountAvailable: itemStatus.amountAvailable,
      amountSold: itemStatus.amountSold,
      availabilityStatus: itemStatus.availabilityStatus,
      item: itemStatus.item,
    });

    this.itemsCollection = this.itemService.addItemToCollectionIfMissing(this.itemsCollection, itemStatus.item);
  }

  protected loadRelationshipsOptions(): void {
    this.itemService
      .query({ filter: 'itemstatus-is-null' })
      .pipe(map((res: HttpResponse<IItem[]>) => res.body ?? []))
      .pipe(map((items: IItem[]) => this.itemService.addItemToCollectionIfMissing(items, this.editForm.get('item')!.value)))
      .subscribe((items: IItem[]) => (this.itemsCollection = items));
  }

  protected createFromForm(): IItemStatus {
    return {
      ...new ItemStatus(),
      id: this.editForm.get(['id'])!.value,
      amountAvailable: this.editForm.get(['amountAvailable'])!.value,
      amountSold: this.editForm.get(['amountSold'])!.value,
      availabilityStatus: this.editForm.get(['availabilityStatus'])!.value,
      item: this.editForm.get(['item'])!.value,
    };
  }
}
