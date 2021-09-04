import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IItemStatus, getItemStatusIdentifier } from '../item-status.model';

export type EntityResponseType = HttpResponse<IItemStatus>;
export type EntityArrayResponseType = HttpResponse<IItemStatus[]>;

@Injectable({ providedIn: 'root' })
export class ItemStatusService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/item-statuses');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/item-statuses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(itemStatus: IItemStatus): Observable<EntityResponseType> {
    return this.http.post<IItemStatus>(this.resourceUrl, itemStatus, { observe: 'response' });
  }

  update(itemStatus: IItemStatus): Observable<EntityResponseType> {
    return this.http.put<IItemStatus>(`${this.resourceUrl}/${getItemStatusIdentifier(itemStatus) as number}`, itemStatus, {
      observe: 'response',
    });
  }

  partialUpdate(itemStatus: IItemStatus): Observable<EntityResponseType> {
    return this.http.patch<IItemStatus>(`${this.resourceUrl}/${getItemStatusIdentifier(itemStatus) as number}`, itemStatus, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IItemStatus>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IItemStatus[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IItemStatus[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addItemStatusToCollectionIfMissing(
    itemStatusCollection: IItemStatus[],
    ...itemStatusesToCheck: (IItemStatus | null | undefined)[]
  ): IItemStatus[] {
    const itemStatuses: IItemStatus[] = itemStatusesToCheck.filter(isPresent);
    if (itemStatuses.length > 0) {
      const itemStatusCollectionIdentifiers = itemStatusCollection.map(itemStatusItem => getItemStatusIdentifier(itemStatusItem)!);
      const itemStatusesToAdd = itemStatuses.filter(itemStatusItem => {
        const itemStatusIdentifier = getItemStatusIdentifier(itemStatusItem);
        if (itemStatusIdentifier == null || itemStatusCollectionIdentifiers.includes(itemStatusIdentifier)) {
          return false;
        }
        itemStatusCollectionIdentifiers.push(itemStatusIdentifier);
        return true;
      });
      return [...itemStatusesToAdd, ...itemStatusCollection];
    }
    return itemStatusCollection;
  }
}
