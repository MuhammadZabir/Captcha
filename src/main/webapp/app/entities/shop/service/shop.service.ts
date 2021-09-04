import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IShop, getShopIdentifier } from '../shop.model';

export type EntityResponseType = HttpResponse<IShop>;
export type EntityArrayResponseType = HttpResponse<IShop[]>;

@Injectable({ providedIn: 'root' })
export class ShopService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shops');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/shops');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .post<IShop>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .put<IShop>(`${this.resourceUrl}/${getShopIdentifier(shop) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .patch<IShop>(`${this.resourceUrl}/${getShopIdentifier(shop) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IShop>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShop[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShop[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addShopToCollectionIfMissing(shopCollection: IShop[], ...shopsToCheck: (IShop | null | undefined)[]): IShop[] {
    const shops: IShop[] = shopsToCheck.filter(isPresent);
    if (shops.length > 0) {
      const shopCollectionIdentifiers = shopCollection.map(shopItem => getShopIdentifier(shopItem)!);
      const shopsToAdd = shops.filter(shopItem => {
        const shopIdentifier = getShopIdentifier(shopItem);
        if (shopIdentifier == null || shopCollectionIdentifiers.includes(shopIdentifier)) {
          return false;
        }
        shopCollectionIdentifiers.push(shopIdentifier);
        return true;
      });
      return [...shopsToAdd, ...shopCollection];
    }
    return shopCollection;
  }

  protected convertDateFromClient(shop: IShop): IShop {
    return Object.assign({}, shop, {
      createdDate: shop.createdDate?.isValid() ? shop.createdDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((shop: IShop) => {
        shop.createdDate = shop.createdDate ? dayjs(shop.createdDate) : undefined;
      });
    }
    return res;
  }
}
