import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CartBasketComponent } from './list/cart-basket.component';
import { CartBasketDetailComponent } from './detail/cart-basket-detail.component';
import { CartBasketUpdateComponent } from './update/cart-basket-update.component';
import { CartBasketDeleteDialogComponent } from './delete/cart-basket-delete-dialog.component';
import { CartBasketRoutingModule } from './route/cart-basket-routing.module';

@NgModule({
  imports: [SharedModule, CartBasketRoutingModule],
  declarations: [CartBasketComponent, CartBasketDetailComponent, CartBasketUpdateComponent, CartBasketDeleteDialogComponent],
  entryComponents: [CartBasketDeleteDialogComponent],
})
export class CartBasketModule {}
