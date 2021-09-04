import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUserExtra, UserExtra } from '../user-extra.model';
import { UserExtraService } from '../service/user-extra.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserType } from 'app/entities/user-type/user-type.model';
import { UserTypeService } from 'app/entities/user-type/service/user-type.service';

@Component({
  selector: 'jhi-user-extra-update',
  templateUrl: './user-extra-update.component.html',
})
export class UserExtraUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  userTypesSharedCollection: IUserType[] = [];

  editForm = this.fb.group({
    id: [],
    billingAddress: [],
    user: [],
    userType: [],
  });

  constructor(
    protected userExtraService: UserExtraService,
    protected userService: UserService,
    protected userTypeService: UserTypeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userExtra }) => {
      this.updateForm(userExtra);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userExtra = this.createFromForm();
    if (userExtra.id !== undefined) {
      this.subscribeToSaveResponse(this.userExtraService.update(userExtra));
    } else {
      this.subscribeToSaveResponse(this.userExtraService.create(userExtra));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackUserTypeById(index: number, item: IUserType): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserExtra>>): void {
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

  protected updateForm(userExtra: IUserExtra): void {
    this.editForm.patchValue({
      id: userExtra.id,
      billingAddress: userExtra.billingAddress,
      user: userExtra.user,
      userType: userExtra.userType,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, userExtra.user);
    this.userTypesSharedCollection = this.userTypeService.addUserTypeToCollectionIfMissing(
      this.userTypesSharedCollection,
      userExtra.userType
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.userTypeService
      .query()
      .pipe(map((res: HttpResponse<IUserType[]>) => res.body ?? []))
      .pipe(
        map((userTypes: IUserType[]) =>
          this.userTypeService.addUserTypeToCollectionIfMissing(userTypes, this.editForm.get('userType')!.value)
        )
      )
      .subscribe((userTypes: IUserType[]) => (this.userTypesSharedCollection = userTypes));
  }

  protected createFromForm(): IUserExtra {
    return {
      ...new UserExtra(),
      id: this.editForm.get(['id'])!.value,
      billingAddress: this.editForm.get(['billingAddress'])!.value,
      user: this.editForm.get(['user'])!.value,
      userType: this.editForm.get(['userType'])!.value,
    };
  }
}
