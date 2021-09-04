import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IItemStatus } from '../item-status.model';

@Component({
  selector: 'jhi-item-status-detail',
  templateUrl: './item-status-detail.component.html',
})
export class ItemStatusDetailComponent implements OnInit {
  itemStatus: IItemStatus | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ itemStatus }) => {
      this.itemStatus = itemStatus;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
