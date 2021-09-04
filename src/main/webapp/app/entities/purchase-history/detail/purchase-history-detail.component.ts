import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPurchaseHistory } from '../purchase-history.model';

@Component({
  selector: 'jhi-purchase-history-detail',
  templateUrl: './purchase-history-detail.component.html',
})
export class PurchaseHistoryDetailComponent implements OnInit {
  purchaseHistory: IPurchaseHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseHistory }) => {
      this.purchaseHistory = purchaseHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
