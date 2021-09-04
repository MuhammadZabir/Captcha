import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IShopReview, ShopReview } from '../shop-review.model';
import { ShopReviewService } from '../service/shop-review.service';

@Injectable({ providedIn: 'root' })
export class ShopReviewRoutingResolveService implements Resolve<IShopReview> {
  constructor(protected service: ShopReviewService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IShopReview> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((shopReview: HttpResponse<ShopReview>) => {
          if (shopReview.body) {
            return of(shopReview.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ShopReview());
  }
}
