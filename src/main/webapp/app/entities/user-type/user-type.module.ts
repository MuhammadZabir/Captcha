import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserTypeComponent } from './list/user-type.component';
import { UserTypeDetailComponent } from './detail/user-type-detail.component';
import { UserTypeUpdateComponent } from './update/user-type-update.component';
import { UserTypeDeleteDialogComponent } from './delete/user-type-delete-dialog.component';
import { UserTypeRoutingModule } from './route/user-type-routing.module';

@NgModule({
  imports: [SharedModule, UserTypeRoutingModule],
  declarations: [UserTypeComponent, UserTypeDetailComponent, UserTypeUpdateComponent, UserTypeDeleteDialogComponent],
  entryComponents: [UserTypeDeleteDialogComponent],
})
export class UserTypeModule {}
