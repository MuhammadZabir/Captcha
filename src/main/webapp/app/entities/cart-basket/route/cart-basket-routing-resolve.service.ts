import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICartBasket, CartBasket } from '../cart-basket.model';
import { CartBasketService } from '../service/cart-basket.service';

@Injectable({ providedIn: 'root' })
export class CartBasketRoutingResolveService implements Resolve<ICartBasket> {
  constructor(protected service: CartBasketService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICartBasket> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cartBasket: HttpResponse<CartBasket>) => {
          if (cartBasket.body) {
            return of(cartBasket.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CartBasket());
  }
}
