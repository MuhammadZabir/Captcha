import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICart, Cart } from '../cart.model';

import { CartService } from './cart.service';

describe('Service Tests', () => {
  describe('Cart Service', () => {
    let service: CartService;
    let httpMock: HttpTestingController;
    let elemDefault: ICart;
    let expectedResult: ICart | ICart[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CartService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        totalPrice: 0,
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

      it('should create a Cart', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Cart()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Cart', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            totalPrice: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Cart', () => {
        const patchObject = Object.assign({}, new Cart());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Cart', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            totalPrice: 1,
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

      it('should delete a Cart', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCartToCollectionIfMissing', () => {
        it('should add a Cart to an empty array', () => {
          const cart: ICart = { id: 123 };
          expectedResult = service.addCartToCollectionIfMissing([], cart);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cart);
        });

        it('should not add a Cart to an array that contains it', () => {
          const cart: ICart = { id: 123 };
          const cartCollection: ICart[] = [
            {
              ...cart,
            },
            { id: 456 },
          ];
          expectedResult = service.addCartToCollectionIfMissing(cartCollection, cart);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Cart to an array that doesn't contain it", () => {
          const cart: ICart = { id: 123 };
          const cartCollection: ICart[] = [{ id: 456 }];
          expectedResult = service.addCartToCollectionIfMissing(cartCollection, cart);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cart);
        });

        it('should add only unique Cart to an array', () => {
          const cartArray: ICart[] = [{ id: 123 }, { id: 456 }, { id: 42407 }];
          const cartCollection: ICart[] = [{ id: 123 }];
          expectedResult = service.addCartToCollectionIfMissing(cartCollection, ...cartArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const cart: ICart = { id: 123 };
          const cart2: ICart = { id: 456 };
          expectedResult = service.addCartToCollectionIfMissing([], cart, cart2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(cart);
          expect(expectedResult).toContain(cart2);
        });

        it('should accept null and undefined values', () => {
          const cart: ICart = { id: 123 };
          expectedResult = service.addCartToCollectionIfMissing([], null, cart, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(cart);
        });

        it('should return initial array if no Cart is added', () => {
          const cartCollection: ICart[] = [{ id: 123 }];
          expectedResult = service.addCartToCollectionIfMissing(cartCollection, undefined, null);
          expect(expectedResult).toEqual(cartCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
