import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IUserExtra, getUserExtraIdentifier } from '../user-extra.model';

export type EntityResponseType = HttpResponse<IUserExtra>;
export type EntityArrayResponseType = HttpResponse<IUserExtra[]>;

@Injectable({ providedIn: 'root' })
export class UserExtraService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-extras');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/user-extras');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userExtra: IUserExtra): Observable<EntityResponseType> {
    return this.http.post<IUserExtra>(this.resourceUrl, userExtra, { observe: 'response' });
  }

  update(userExtra: IUserExtra): Observable<EntityResponseType> {
    return this.http.put<IUserExtra>(`${this.resourceUrl}/${getUserExtraIdentifier(userExtra) as number}`, userExtra, {
      observe: 'response',
    });
  }

  partialUpdate(userExtra: IUserExtra): Observable<EntityResponseType> {
    return this.http.patch<IUserExtra>(`${this.resourceUrl}/${getUserExtraIdentifier(userExtra) as number}`, userExtra, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserExtra>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserExtra[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserExtra[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addUserExtraToCollectionIfMissing(
    userExtraCollection: IUserExtra[],
    ...userExtrasToCheck: (IUserExtra | null | undefined)[]
  ): IUserExtra[] {
    const userExtras: IUserExtra[] = userExtrasToCheck.filter(isPresent);
    if (userExtras.length > 0) {
      const userExtraCollectionIdentifiers = userExtraCollection.map(userExtraItem => getUserExtraIdentifier(userExtraItem)!);
      const userExtrasToAdd = userExtras.filter(userExtraItem => {
        const userExtraIdentifier = getUserExtraIdentifier(userExtraItem);
        if (userExtraIdentifier == null || userExtraCollectionIdentifiers.includes(userExtraIdentifier)) {
          return false;
        }
        userExtraCollectionIdentifiers.push(userExtraIdentifier);
        return true;
      });
      return [...userExtrasToAdd, ...userExtraCollection];
    }
    return userExtraCollection;
  }
}
