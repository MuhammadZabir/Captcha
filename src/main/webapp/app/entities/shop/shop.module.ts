import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShopComponent } from './list/shop.component';
import { ShopDetailComponent } from './detail/shop-detail.component';
import { ShopUpdateComponent } from './update/shop-update.component';
import { ShopDeleteDialogComponent } from './delete/shop-delete-dialog.component';
import { ShopRoutingModule } from './route/shop-routing.module';

@NgModule({
  imports: [SharedModule, ShopRoutingModule],
  declarations: [ShopComponent, ShopDetailComponent, ShopUpdateComponent, ShopDeleteDialogComponent],
  entryComponents: [ShopDeleteDialogComponent],
})
export class ShopModule {}
