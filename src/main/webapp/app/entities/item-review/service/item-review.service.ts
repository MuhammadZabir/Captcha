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
import { IItemReview, getItemReviewIdentifier } from '../item-review.model';

export type EntityResponseType = HttpResponse<IItemReview>;
export type EntityArrayResponseType = HttpResponse<IItemReview[]>;

@Injectable({ providedIn: 'root' })
export class ItemReviewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/item-reviews');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/item-reviews');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(itemReview: IItemReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(itemReview);
    return this.http
      .post<IItemReview>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(itemReview: IItemReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(itemReview);
    return this.http
      .put<IItemReview>(`${this.resourceUrl}/${getItemReviewIdentifier(itemReview) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(itemReview: IItemReview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(itemReview);
    return this.http
      .patch<IItemReview>(`${this.resourceUrl}/${getItemReviewIdentifier(itemReview) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IItemReview>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IItemReview[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IItemReview[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addItemReviewToCollectionIfMissing(
    itemReviewCollection: IItemReview[],
    ...itemReviewsToCheck: (IItemReview | null | undefined)[]
  ): IItemReview[] {
    const itemReviews: IItemReview[] = itemReviewsToCheck.filter(isPresent);
    if (itemReviews.length > 0) {
      const itemReviewCollectionIdentifiers = itemReviewCollection.map(itemReviewItem => getItemReviewIdentifier(itemReviewItem)!);
      const itemReviewsToAdd = itemReviews.filter(itemReviewItem => {
        const itemReviewIdentifier = getItemReviewIdentifier(itemReviewItem);
        if (itemReviewIdentifier == null || itemReviewCollectionIdentifiers.includes(itemReviewIdentifier)) {
          return false;
        }
        itemReviewCollectionIdentifiers.push(itemReviewIdentifier);
        return true;
      });
      return [...itemReviewsToAdd, ...itemReviewCollection];
    }
    return itemReviewCollection;
  }

  protected convertDateFromClient(itemReview: IItemReview): IItemReview {
    return Object.assign({}, itemReview, {
      reviewDate: itemReview.reviewDate?.isValid() ? itemReview.reviewDate.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((itemReview: IItemReview) => {
        itemReview.reviewDate = itemReview.reviewDate ? dayjs(itemReview.reviewDate) : undefined;
      });
    }
    return res;
  }
}
