import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ItemStatusComponent } from './list/item-status.component';
import { ItemStatusDetailComponent } from './detail/item-status-detail.component';
import { ItemStatusUpdateComponent } from './update/item-status-update.component';
import { ItemStatusDeleteDialogComponent } from './delete/item-status-delete-dialog.component';
import { ItemStatusRoutingModule } from './route/item-status-routing.module';

@NgModule({
  imports: [SharedModule, ItemStatusRoutingModule],
  declarations: [ItemStatusComponent, ItemStatusDetailComponent, ItemStatusUpdateComponent, ItemStatusDeleteDialogComponent],
  entryComponents: [ItemStatusDeleteDialogComponent],
})
export class ItemStatusModule {}
