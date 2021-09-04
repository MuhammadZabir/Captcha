import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IItemReview, ItemReview } from '../item-review.model';
import { ItemReviewService } from '../service/item-review.service';

@Injectable({ providedIn: 'root' })
export class ItemReviewRoutingResolveService implements Resolve<IItemReview> {
  constructor(protected service: ItemReviewService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IItemReview> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((itemReview: HttpResponse<ItemReview>) => {
          if (itemReview.body) {
            return of(itemReview.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ItemReview());
  }
}
