import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IImage, getImageIdentifier } from '../image.model';

export type EntityResponseType = HttpResponse<IImage>;
export type EntityArrayResponseType = HttpResponse<IImage[]>;

@Injectable({ providedIn: 'root' })
export class ImageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/images');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/images');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService, protected domSanitizer: DomSanitizer) {}

  create(image: IImage): Observable<EntityResponseType> {
    return this.http.post<IImage>(this.resourceUrl, image, { observe: 'response' });
  }

  update(image: IImage): Observable<EntityResponseType> {
    return this.http.put<IImage>(`${this.resourceUrl}/${getImageIdentifier(image) as number}`, image, { observe: 'response' });
  }

  partialUpdate(image: IImage): Observable<EntityResponseType> {
    return this.http.patch<IImage>(`${this.resourceUrl}/${getImageIdentifier(image) as number}`, image, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getImage(directory: string): Observable<Blob> {
    return this.http.post("api/utility/get-image", directory, { responseType: 'blob' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IImage[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addImageToCollectionIfMissing(imageCollection: IImage[], ...imagesToCheck: (IImage | null | undefined)[]): IImage[] {
    const images: IImage[] = imagesToCheck.filter(isPresent);
    if (images.length > 0) {
      const imageCollectionIdentifiers = imageCollection.map(imageItem => getImageIdentifier(imageItem)!);
      const imagesToAdd = images.filter(imageItem => {
        const imageIdentifier = getImageIdentifier(imageItem);
        if (imageIdentifier == null || imageCollectionIdentifiers.includes(imageIdentifier)) {
          return false;
        }
        imageCollectionIdentifiers.push(imageIdentifier);
        return true;
      });
      return [...imagesToAdd, ...imageCollection];
    }
    return imageCollection;
  }
}
