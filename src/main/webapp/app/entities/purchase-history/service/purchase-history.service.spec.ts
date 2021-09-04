import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { IPurchaseHistory, PurchaseHistory } from '../purchase-history.model';

import { PurchaseHistoryService } from './purchase-history.service';

describe('Service Tests', () => {
  describe('PurchaseHistory Service', () => {
    let service: PurchaseHistoryService;
    let httpMock: HttpTestingController;
    let elemDefault: IPurchaseHistory;
    let expectedResult: IPurchaseHistory | IPurchaseHistory[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PurchaseHistoryService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        purchaseDate: currentDate,
        shippingDate: currentDate,
        billingAddress: 'AAAAAAA',
        paymentStatus: PaymentStatus.PAID,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            purchaseDate: currentDate.format(DATE_FORMAT),
            shippingDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a PurchaseHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            purchaseDate: currentDate.format(DATE_FORMAT),
            shippingDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            purchaseDate: currentDate,
            shippingDate: currentDate,
          },
          returnedFromService
        );

        service.create(new PurchaseHistory()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a PurchaseHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            purchaseDate: currentDate.format(DATE_FORMAT),
            shippingDate: currentDate.format(DATE_FORMAT),
            billingAddress: 'BBBBBB',
            paymentStatus: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            purchaseDate: currentDate,
            shippingDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a PurchaseHistory', () => {
        const patchObject = Object.assign(
          {
            billingAddress: 'BBBBBB',
            paymentStatus: 'BBBBBB',
          },
          new PurchaseHistory()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            purchaseDate: currentDate,
            shippingDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of PurchaseHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            purchaseDate: currentDate.format(DATE_FORMAT),
            shippingDate: currentDate.format(DATE_FORMAT),
            billingAddress: 'BBBBBB',
            paymentStatus: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            purchaseDate: currentDate,
            shippingDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a PurchaseHistory', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPurchaseHistoryToCollectionIfMissing', () => {
        it('should add a PurchaseHistory to an empty array', () => {
          const purchaseHistory: IPurchaseHistory = { id: 123 };
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing([], purchaseHistory);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(purchaseHistory);
        });

        it('should not add a PurchaseHistory to an array that contains it', () => {
          const purchaseHistory: IPurchaseHistory = { id: 123 };
          const purchaseHistoryCollection: IPurchaseHistory[] = [
            {
              ...purchaseHistory,
            },
            { id: 456 },
          ];
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing(purchaseHistoryCollection, purchaseHistory);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a PurchaseHistory to an array that doesn't contain it", () => {
          const purchaseHistory: IPurchaseHistory = { id: 123 };
          const purchaseHistoryCollection: IPurchaseHistory[] = [{ id: 456 }];
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing(purchaseHistoryCollection, purchaseHistory);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(purchaseHistory);
        });

        it('should add only unique PurchaseHistory to an array', () => {
          const purchaseHistoryArray: IPurchaseHistory[] = [{ id: 123 }, { id: 456 }, { id: 43494 }];
          const purchaseHistoryCollection: IPurchaseHistory[] = [{ id: 123 }];
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing(purchaseHistoryCollection, ...purchaseHistoryArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const purchaseHistory: IPurchaseHistory = { id: 123 };
          const purchaseHistory2: IPurchaseHistory = { id: 456 };
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing([], purchaseHistory, purchaseHistory2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(purchaseHistory);
          expect(expectedResult).toContain(purchaseHistory2);
        });

        it('should accept null and undefined values', () => {
          const purchaseHistory: IPurchaseHistory = { id: 123 };
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing([], null, purchaseHistory, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(purchaseHistory);
        });

        it('should return initial array if no PurchaseHistory is added', () => {
          const purchaseHistoryCollection: IPurchaseHistory[] = [{ id: 123 }];
          expectedResult = service.addPurchaseHistoryToCollectionIfMissing(purchaseHistoryCollection, undefined, null);
          expect(expectedResult).toEqual(purchaseHistoryCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
