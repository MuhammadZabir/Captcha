import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserType } from '../user-type.model';

@Component({
  selector: 'jhi-user-type-detail',
  templateUrl: './user-type-detail.component.html',
})
export class UserTypeDetailComponent implements OnInit {
  userType: IUserType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userType }) => {
      this.userType = userType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
