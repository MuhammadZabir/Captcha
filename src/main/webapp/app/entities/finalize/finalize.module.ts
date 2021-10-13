import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CartFinalizeComponent } from './cart-finalize.component';
import { FinalizeRoutingModule } from './route/finalize-routing.module';

@NgModule({
  imports: [SharedModule, FinalizeRoutingModule],
  declarations: [CartFinalizeComponent],
  entryComponents: [],
})
export class FinalizeModule {}
