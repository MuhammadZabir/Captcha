import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserType, UserType } from '../user-type.model';
import { UserTypeService } from '../service/user-type.service';

@Injectable({ providedIn: 'root' })
export class UserTypeRoutingResolveService implements Resolve<IUserType> {
  constructor(protected service: UserTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userType: HttpResponse<UserType>) => {
          if (userType.body) {
            return of(userType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserType());
  }
}
