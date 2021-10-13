import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CartFinalizeComponent } from '../cart-finalize.component';

const finalizeRoute: Routes = [
  {
    path: '',
    component: CartFinalizeComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(finalizeRoute)],
  exports: [RouterModule],
})
export class FinalizeRoutingModule {}
