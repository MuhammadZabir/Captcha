import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchaseHistory, PurchaseHistory } from '../purchase-history.model';
import { PurchaseHistoryService } from '../service/purchase-history.service';

@Injectable({ providedIn: 'root' })
export class PurchaseHistoryRoutingResolveService implements Resolve<IPurchaseHistory> {
  constructor(protected service: PurchaseHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPurchaseHistory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((purchaseHistory: HttpResponse<PurchaseHistory>) => {
          if (purchaseHistory.body) {
            return of(purchaseHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PurchaseHistory());
  }
}
