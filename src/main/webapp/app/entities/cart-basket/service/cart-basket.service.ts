import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICartBasket, getCartBasketIdentifier } from '../cart-basket.model';

export type EntityResponseType = HttpResponse<ICartBasket>;
export type EntityArrayResponseType = HttpResponse<ICartBasket[]>;

@Injectable({ providedIn: 'root' })
export class CartBasketService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cart-baskets');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cart-baskets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cartBasket: ICartBasket): Observable<EntityResponseType> {
    return this.http.post<ICartBasket>(this.resourceUrl, cartBasket, { observe: 'response' });
  }

  update(cartBasket: ICartBasket): Observable<EntityResponseType> {
    return this.http.put<ICartBasket>(`${this.resourceUrl}/${getCartBasketIdentifier(cartBasket) as number}`, cartBasket, {
      observe: 'response',
    });
  }

  partialUpdate(cartBasket: ICartBasket): Observable<EntityResponseType> {
    return this.http.patch<ICartBasket>(`${this.resourceUrl}/${getCartBasketIdentifier(cartBasket) as number}`, cartBasket, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICartBasket>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICartBasket[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICartBasket[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCartBasketToCollectionIfMissing(
    cartBasketCollection: ICartBasket[],
    ...cartBasketsToCheck: (ICartBasket | null | undefined)[]
  ): ICartBasket[] {
    const cartBaskets: ICartBasket[] = cartBasketsToCheck.filter(isPresent);
    if (cartBaskets.length > 0) {
      const cartBasketCollectionIdentifiers = cartBasketCollection.map(cartBasketItem => getCartBasketIdentifier(cartBasketItem)!);
      const cartBasketsToAdd = cartBaskets.filter(cartBasketItem => {
        const cartBasketIdentifier = getCartBasketIdentifier(cartBasketItem);
        if (cartBasketIdentifier == null || cartBasketCollectionIdentifiers.includes(cartBasketIdentifier)) {
          return false;
        }
        cartBasketCollectionIdentifiers.push(cartBasketIdentifier);
        return true;
      });
      return [...cartBasketsToAdd, ...cartBasketCollection];
    }
    return cartBasketCollection;
  }
}
