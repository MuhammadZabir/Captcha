import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IPurchaseHistory, getPurchaseHistoryIdentifier } from '../purchase-history.model';

export type EntityResponseType = HttpResponse<IPurchaseHistory>;
export type EntityArrayResponseType = HttpResponse<IPurchaseHistory[]>;

@Injectable({ providedIn: 'root' })
export class PurchaseHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/purchase-histories');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/purchase-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(purchaseHistory: IPurchaseHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseHistory);
    return this.http
      .post<IPurchaseHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(purchaseHistory: IPurchaseHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseHistory);
    return this.http
      .put<IPurchaseHistory>(`${this.resourceUrl}/${getPurchaseHistoryIdentifier(purchaseHistory) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(purchaseHistory: IPurchaseHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseHistory);
    return this.http
      .patch<IPurchaseHistory>(`${this.resourceUrl}/${getPurchaseHistoryIdentifier(purchaseHistory) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPurchaseHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPurchaseHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPurchaseHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addPurchaseHistoryToCollectionIfMissing(
    purchaseHistoryCollection: IPurchaseHistory[],
    ...purchaseHistoriesToCheck: (IPurchaseHistory | null | undefined)[]
  ): IPurchaseHistory[] {
    const purchaseHistories: IPurchaseHistory[] = purchaseHistoriesToCheck.filter(isPresent);
    if (purchaseHistories.length > 0) {
      const purchaseHistoryCollectionIdentifiers = purchaseHistoryCollection.map(
        purchaseHistoryItem => getPurchaseHistoryIdentifier(purchaseHistoryItem)!
      );
      const purchaseHistoriesToAdd = purchaseHistories.filter(purchaseHistoryItem => {
        const purchaseHistoryIdentifier = getPurchaseHistoryIdentifier(purchaseHistoryItem);
        if (purchaseHistoryIdentifier == null || purchaseHistoryCollectionIdentifiers.includes(purchaseHistoryIdentifier)) {
          return false;
        }
        purchaseHistoryCollectionIdentifiers.push(purchaseHistoryIdentifier);
        return true;
      });
      return [...purchaseHistoriesToAdd, ...purchaseHistoryCollection];
    }
    return purchaseHistoryCollection;
  }

  protected convertDateFromClient(purchaseHistory: IPurchaseHistory): IPurchaseHistory {
    return Object.assign({}, purchaseHistory, {
      purchaseDate: purchaseHistory.purchaseDate?.isValid() ? purchaseHistory.purchaseDate.format(DATE_FORMAT) : undefined,
      shippingDate: purchaseHistory.shippingDate?.isValid() ? purchaseHistory.shippingDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.purchaseDate = res.body.purchaseDate ? dayjs(res.body.purchaseDate) : undefined;
      res.body.shippingDate = res.body.shippingDate ? dayjs(res.body.shippingDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((purchaseHistory: IPurchaseHistory) => {
        purchaseHistory.purchaseDate = purchaseHistory.purchaseDate ? dayjs(purchaseHistory.purchaseDate) : undefined;
        purchaseHistory.shippingDate = purchaseHistory.shippingDate ? dayjs(purchaseHistory.shippingDate) : undefined;
      });
    }
    return res;
  }
}
