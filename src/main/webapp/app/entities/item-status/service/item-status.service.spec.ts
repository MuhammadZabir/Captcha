import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AvailabilityStatus } from 'app/entities/enumerations/availability-status.model';
import { IItemStatus, ItemStatus } from '../item-status.model';

import { ItemStatusService } from './item-status.service';

describe('Service Tests', () => {
  describe('ItemStatus Service', () => {
    let service: ItemStatusService;
    let httpMock: HttpTestingController;
    let elemDefault: IItemStatus;
    let expectedResult: IItemStatus | IItemStatus[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ItemStatusService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        amountAvailable: 0,
        amountSold: 0,
        availabilityStatus: AvailabilityStatus.AVAILABLE,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a ItemStatus', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new ItemStatus()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ItemStatus', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amountAvailable: 1,
            amountSold: 1,
            availabilityStatus: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a ItemStatus', () => {
        const patchObject = Object.assign(
          {
            amountSold: 1,
          },
          new ItemStatus()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of ItemStatus', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amountAvailable: 1,
            amountSold: 1,
            availabilityStatus: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a ItemStatus', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addItemStatusToCollectionIfMissing', () => {
        it('should add a ItemStatus to an empty array', () => {
          const itemStatus: IItemStatus = { id: 123 };
          expectedResult = service.addItemStatusToCollectionIfMissing([], itemStatus);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(itemStatus);
        });

        it('should not add a ItemStatus to an array that contains it', () => {
          const itemStatus: IItemStatus = { id: 123 };
          const itemStatusCollection: IItemStatus[] = [
            {
              ...itemStatus,
            },
            { id: 456 },
          ];
          expectedResult = service.addItemStatusToCollectionIfMissing(itemStatusCollection, itemStatus);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ItemStatus to an array that doesn't contain it", () => {
          const itemStatus: IItemStatus = { id: 123 };
          const itemStatusCollection: IItemStatus[] = [{ id: 456 }];
          expectedResult = service.addItemStatusToCollectionIfMissing(itemStatusCollection, itemStatus);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(itemStatus);
        });

        it('should add only unique ItemStatus to an array', () => {
          const itemStatusArray: IItemStatus[] = [{ id: 123 }, { id: 456 }, { id: 74112 }];
          const itemStatusCollection: IItemStatus[] = [{ id: 123 }];
          expectedResult = service.addItemStatusToCollectionIfMissing(itemStatusCollection, ...itemStatusArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const itemStatus: IItemStatus = { id: 123 };
          const itemStatus2: IItemStatus = { id: 456 };
          expectedResult = service.addItemStatusToCollectionIfMissing([], itemStatus, itemStatus2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(itemStatus);
          expect(expectedResult).toContain(itemStatus2);
        });

        it('should accept null and undefined values', () => {
          const itemStatus: IItemStatus = { id: 123 };
          expectedResult = service.addItemStatusToCollectionIfMissing([], null, itemStatus, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(itemStatus);
        });

        it('should return initial array if no ItemStatus is added', () => {
          const itemStatusCollection: IItemStatus[] = [{ id: 123 }];
          expectedResult = service.addItemStatusToCollectionIfMissing(itemStatusCollection, undefined, null);
          expect(expectedResult).toEqual(itemStatusCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
