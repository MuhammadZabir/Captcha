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
import { IShopReview, getShopReviewIdentifier } from '../shop-review.model';

export type EntityResponseType = HttpResponse<IShopReview>;
export type EntityArrayResponseType = HttpResponse<IShopReview[]>;

@Injectable({ providedIn: 'root' })
export class ShopReviewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shop-reviews');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/shop-reviews');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shopReview: IShopReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shopReview);
    return this.http
      .post<IShopReview>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(shopReview: IShopReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shopReview);
    return this.http
      .put<IShopReview>(`${this.resourceUrl}/${getShopReviewIdentifier(shopReview) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(shopReview: IShopReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shopReview);
    return this.http
      .patch<IShopReview>(`${this.resourceUrl}/${getShopReviewIdentifier(shopReview) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IShopReview>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShopReview[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShopReview[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addShopReviewToCollectionIfMissing(
    shopReviewCollection: IShopReview[],
    ...shopReviewsToCheck: (IShopReview | null | undefined)[]
  ): IShopReview[] {
    const shopReviews: IShopReview[] = shopReviewsToCheck.filter(isPresent);
    if (shopReviews.length > 0) {
      const shopReviewCollectionIdentifiers = shopReviewCollection.map(shopReviewItem => getShopReviewIdentifier(shopReviewItem)!);
      const shopReviewsToAdd = shopReviews.filter(shopReviewItem => {
        const shopReviewIdentifier = getShopReviewIdentifier(shopReviewItem);
        if (shopReviewIdentifier == null || shopReviewCollectionIdentifiers.includes(shopReviewIdentifier)) {
          return false;
        }
        shopReviewCollectionIdentifiers.push(shopReviewIdentifier);
        return true;
      });
      return [...shopReviewsToAdd, ...shopReviewCollection];
    }
    return shopReviewCollection;
  }

  protected convertDateFromClient(shopReview: IShopReview): IShopReview {
    return Object.assign({}, shopReview, {
      reviewDate: shopReview.reviewDate?.isValid() ? shopReview.reviewDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.reviewDate = res.body.reviewDate ? dayjs(res.body.reviewDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((shopReview: IShopReview) => {
        shopReview.reviewDate = shopReview.reviewDate ? dayjs(shopReview.reviewDate) : undefined;
      });
    }
    return res;
  }
}
