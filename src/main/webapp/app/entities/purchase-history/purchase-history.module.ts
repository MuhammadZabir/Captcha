import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PurchaseHistoryComponent } from './list/purchase-history.component';
import { PurchaseHistoryDetailComponent } from './detail/purchase-history-detail.component';
import { PurchaseHistoryUpdateComponent } from './update/purchase-history-update.component';
import { PurchaseHistoryDeleteDialogComponent } from './delete/purchase-history-delete-dialog.component';
import { PurchaseHistoryRoutingModule } from './route/purchase-history-routing.module';

@NgModule({
  imports: [SharedModule, PurchaseHistoryRoutingModule],
  declarations: [
    PurchaseHistoryComponent,
    PurchaseHistoryDetailComponent,
    PurchaseHistoryUpdateComponent,
    PurchaseHistoryDeleteDialogComponent,
  ],
  entryComponents: [PurchaseHistoryDeleteDialogComponent],
})
export class PurchaseHistoryModule {}
