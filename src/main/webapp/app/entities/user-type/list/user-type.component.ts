import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserType } from '../user-type.model';
import { UserTypeService } from '../service/user-type.service';
import { UserTypeDeleteDialogComponent } from '../delete/user-type-delete-dialog.component';

@Component({
  selector: 'jhi-user-type',
  templateUrl: './user-type.component.html',
})
export class UserTypeComponent implements OnInit {
  userTypes?: IUserType[];
  isLoading = false;
  currentSearch: string;

  constructor(protected userTypeService: UserTypeService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.userTypeService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IUserType[]>) => {
            this.isLoading = false;
            this.userTypes = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.userTypeService.query().subscribe(
      (res: HttpResponse<IUserType[]>) => {
        this.isLoading = false;
        this.userTypes = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IUserType): number {
    return item.id!;
  }

  delete(userType: IUserType): void {
    const modalRef = this.modalService.open(UserTypeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userType = userType;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
