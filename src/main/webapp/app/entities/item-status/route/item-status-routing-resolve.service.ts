import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IItemStatus, ItemStatus } from '../item-status.model';
import { ItemStatusService } from '../service/item-status.service';

@Injectable({ providedIn: 'root' })
export class ItemStatusRoutingResolveService implements Resolve<IItemStatus> {
  constructor(protected service: ItemStatusService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IItemStatus> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((itemStatus: HttpResponse<ItemStatus>) => {
          if (itemStatus.body) {
            return of(itemStatus.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ItemStatus());
  }
}
