import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IShopReview, ShopReview } from '../shop-review.model';

import { ShopReviewService } from './shop-review.service';

describe('Service Tests', () => {
  describe('ShopReview Service', () => {
    let service: ShopReviewService;
    let httpMock: HttpTestingController;
    let elemDefault: IShopReview;
    let expectedResult: IShopReview | IShopReview[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ShopReviewService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        description: 'AAAAAAA',
        rating: 0,
        reviewDate: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            reviewDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a ShopReview', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            reviewDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            reviewDate: currentDate,
          },
          returnedFromService
        );

        service.create(new ShopReview()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ShopReview', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            description: 'BBBBBB',
            rating: 1,
            reviewDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            reviewDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a ShopReview', () => {
        const patchObject = Object.assign(
          {
            description: 'BBBBBB',
            reviewDate: currentDate.format(DATE_FORMAT),
          },
          new ShopReview()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            reviewDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of ShopReview', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            description: 'BBBBBB',
            rating: 1,
            reviewDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            reviewDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a ShopReview', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addShopReviewToCollectionIfMissing', () => {
        it('should add a ShopReview to an empty array', () => {
          const shopReview: IShopReview = { id: 123 };
          expectedResult = service.addShopReviewToCollectionIfMissing([], shopReview);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(shopReview);
        });

        it('should not add a ShopReview to an array that contains it', () => {
          const shopReview: IShopReview = { id: 123 };
          const shopReviewCollection: IShopReview[] = [
            {
              ...shopReview,
            },
            { id: 456 },
          ];
          expectedResult = service.addShopReviewToCollectionIfMissing(shopReviewCollection, shopReview);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ShopReview to an array that doesn't contain it", () => {
          const shopReview: IShopReview = { id: 123 };
          const shopReviewCollection: IShopReview[] = [{ id: 456 }];
          expectedResult = service.addShopReviewToCollectionIfMissing(shopReviewCollection, shopReview);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(shopReview);
        });

        it('should add only unique ShopReview to an array', () => {
          const shopReviewArray: IShopReview[] = [{ id: 123 }, { id: 456 }, { id: 9542 }];
          const shopReviewCollection: IShopReview[] = [{ id: 123 }];
          expectedResult = service.addShopReviewToCollectionIfMissing(shopReviewCollection, ...shopReviewArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const shopReview: IShopReview = { id: 123 };
          const shopReview2: IShopReview = { id: 456 };
          expectedResult = service.addShopReviewToCollectionIfMissing([], shopReview, shopReview2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(shopReview);
          expect(expectedResult).toContain(shopReview2);
        });

        it('should accept null and undefined values', () => {
          const shopReview: IShopReview = { id: 123 };
          expectedResult = service.addShopReviewToCollectionIfMissing([], null, shopReview, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(shopReview);
        });

        it('should return initial array if no ShopReview is added', () => {
          const shopReviewCollection: IShopReview[] = [{ id: 123 }];
          expectedResult = service.addShopReviewToCollectionIfMissing(shopReviewCollection, undefined, null);
          expect(expectedResult).toEqual(shopReviewCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
