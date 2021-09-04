import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IUserType, getUserTypeIdentifier } from '../user-type.model';

export type EntityResponseType = HttpResponse<IUserType>;
export type EntityArrayResponseType = HttpResponse<IUserType[]>;

@Injectable({ providedIn: 'root' })
export class UserTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/user-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userType: IUserType): Observable<EntityResponseType> {
    return this.http.post<IUserType>(this.resourceUrl, userType, { observe: 'response' });
  }

  update(userType: IUserType): Observable<EntityResponseType> {
    return this.http.put<IUserType>(`${this.resourceUrl}/${getUserTypeIdentifier(userType) as number}`, userType, { observe: 'response' });
  }

  partialUpdate(userType: IUserType): Observable<EntityResponseType> {
    return this.http.patch<IUserType>(`${this.resourceUrl}/${getUserTypeIdentifier(userType) as number}`, userType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserType[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addUserTypeToCollectionIfMissing(userTypeCollection: IUserType[], ...userTypesToCheck: (IUserType | null | undefined)[]): IUserType[] {
    const userTypes: IUserType[] = userTypesToCheck.filter(isPresent);
    if (userTypes.length > 0) {
      const userTypeCollectionIdentifiers = userTypeCollection.map(userTypeItem => getUserTypeIdentifier(userTypeItem)!);
      const userTypesToAdd = userTypes.filter(userTypeItem => {
        const userTypeIdentifier = getUserTypeIdentifier(userTypeItem);
        if (userTypeIdentifier == null || userTypeCollectionIdentifiers.includes(userTypeIdentifier)) {
          return false;
        }
        userTypeCollectionIdentifiers.push(userTypeIdentifier);
        return true;
      });
      return [...userTypesToAdd, ...userTypeCollection];
    }
    return userTypeCollection;
  }
}
