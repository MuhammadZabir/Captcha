import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IItemReview, ItemReview } from '../item-review.model';

import { ItemReviewService } from './item-review.service';

describe('Service Tests', () => {
  describe('ItemReview Service', () => {
    let service: ItemReviewService;
    let httpMock: HttpTestingController;
    let elemDefault: IItemReview;
    let expectedResult: IItemReview | IItemReview[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ItemReviewService);
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

      it('should create a ItemReview', () => {
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

        service.create(new ItemReview()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ItemReview', () => {
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

      it('should partial update a ItemReview', () => {
        const patchObject = Object.assign(
          {
            rating: 1,
          },
          new ItemReview()
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

      it('should return a list of ItemReview', () => {
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

      it('should delete a ItemReview', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addItemReviewToCollectionIfMissing', () => {
        it('should add a ItemReview to an empty array', () => {
          const itemReview: IItemReview = { id: 123 };
          expectedResult = service.addItemReviewToCollectionIfMissing([], itemReview);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(itemReview);
        });

        it('should not add a ItemReview to an array that contains it', () => {
          const itemReview: IItemReview = { id: 123 };
          const itemReviewCollection: IItemReview[] = [
            {
              ...itemReview,
            },
            { id: 456 },
          ];
          expectedResult = service.addItemReviewToCollectionIfMissing(itemReviewCollection, itemReview);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ItemReview to an array that doesn't contain it", () => {
          const itemReview: IItemReview = { id: 123 };
          const itemReviewCollection: IItemReview[] = [{ id: 456 }];
          expectedResult = service.addItemReviewToCollectionIfMissing(itemReviewCollection, itemReview);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(itemReview);
        });

        it('should add only unique ItemReview to an array', () => {
          const itemReviewArray: IItemReview[] = [{ id: 123 }, { id: 456 }, { id: 68313 }];
          const itemReviewCollection: IItemReview[] = [{ id: 123 }];
          expectedResult = service.addItemReviewToCollectionIfMissing(itemReviewCollection, ...itemReviewArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const itemReview: IItemReview = { id: 123 };
          const itemReview2: IItemReview = { id: 456 };
          expectedResult = service.addItemReviewToCollectionIfMissing([], itemReview, itemReview2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(itemReview);
          expect(expectedResult).toContain(itemReview2);
        });

        it('should accept null and undefined values', () => {
          const itemReview: IItemReview = { id: 123 };
          expectedResult = service.addItemReviewToCollectionIfMissing([], null, itemReview, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(itemReview);
        });

        it('should return initial array if no ItemReview is added', () => {
          const itemReviewCollection: IItemReview[] = [{ id: 123 }];
          expectedResult = service.addItemReviewToCollectionIfMissing(itemReviewCollection, undefined, null);
          expect(expectedResult).toEqual(itemReviewCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
