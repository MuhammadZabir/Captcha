import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICartBasket, CartBasket } from '../cart-basket.model';

import { CartBasketService } from './cart-basket.service';

describe('Service Tests', () => {
  describe('CartBasket Service', () => {
    let service: CartBasketService;
    let httpMock: HttpTestingController;
    let elemDefault: ICartBasket;
    let expectedResult: ICartBasket | ICartBasket[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CartBasketService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        amount: 0,
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

      it('should create a CartBasket', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new CartBasket()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a CartBasket', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amount: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a CartBasket', () => {
        const patchObject = Object.assign({}, new CartBasket());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of CartBasket', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            amount: 1,
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

      it('should delete a CartBasket', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCartBasketToCollectionIfMissing', () => {
        it('should add a CartBasket to an empty array', () => {
          const cartBasket: ICartBasket = { id: 123 };
          expectedResult = service.addCartBasketToCollectionIfMissing([], cartBasket);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartBasket);
        });

        it('should not add a CartBasket to an array that contains it', () => {
          const cartBasket: ICartBasket = { id: 123 };
          const cartBasketCollection: ICartBasket[] = [
            {
              ...cartBasket,
            },
            { id: 456 },
          ];
          expectedResult = service.addCartBasketToCollectionIfMissing(cartBasketCollection, cartBasket);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a CartBasket to an array that doesn't contain it", () => {
          const cartBasket: ICartBasket = { id: 123 };
          const cartBasketCollection: ICartBasket[] = [{ id: 456 }];
          expectedResult = service.addCartBasketToCollectionIfMissing(cartBasketCollection, cartBasket);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartBasket);
        });

        it('should add only unique CartBasket to an array', () => {
          const cartBasketArray: ICartBasket[] = [{ id: 123 }, { id: 456 }, { id: 21966 }];
          const cartBasketCollection: ICartBasket[] = [{ id: 123 }];
          expectedResult = service.addCartBasketToCollectionIfMissing(cartBasketCollection, ...cartBasketArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const cartBasket: ICartBasket = { id: 123 };
          const cartBasket2: ICartBasket = { id: 456 };
          expectedResult = service.addCartBasketToCollectionIfMissing([], cartBasket, cartBasket2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cartBasket);
          expect(expectedResult).toContain(cartBasket2);
        });

        it('should accept null and undefined values', () => {
          const cartBasket: ICartBasket = { id: 123 };
          expectedResult = service.addCartBasketToCollectionIfMissing([], null, cartBasket, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cartBasket);
        });

        it('should return initial array if no CartBasket is added', () => {
          const cartBasketCollection: ICartBasket[] = [{ id: 123 }];
          expectedResult = service.addCartBasketToCollectionIfMissing(cartBasketCollection, undefined, null);
          expect(expectedResult).toEqual(cartBasketCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
