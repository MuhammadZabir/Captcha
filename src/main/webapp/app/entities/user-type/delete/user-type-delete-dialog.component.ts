import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserType } from '../user-type.model';
import { UserTypeService } from '../service/user-type.service';

@Component({
  templateUrl: './user-type-delete-dialog.component.html',
})
export class UserTypeDeleteDialogComponent {
  userType?: IUserType;

  constructor(protected userTypeService: UserTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
