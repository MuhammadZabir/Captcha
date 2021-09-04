import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUserType, UserType } from '../user-type.model';
import { UserTypeService } from '../service/user-type.service';

@Component({
  selector: 'jhi-user-type-update',
  templateUrl: './user-type-update.component.html',
})
export class UserTypeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
  });

  constructor(protected userTypeService: UserTypeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userType }) => {
      this.updateForm(userType);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userType = this.createFromForm();
    if (userType.id !== undefined) {
      this.subscribeToSaveResponse(this.userTypeService.update(userType));
    } else {
      this.subscribeToSaveResponse(this.userTypeService.create(userType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserType>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userType: IUserType): void {
    this.editForm.patchValue({
      id: userType.id,
      name: userType.name,
      description: userType.description,
    });
  }

  protected createFromForm(): IUserType {
    return {
      ...new UserType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
