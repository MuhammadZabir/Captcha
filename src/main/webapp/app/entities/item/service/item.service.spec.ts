import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IItem, Item } from '../item.model';

import { ItemService } from './item.service';

describe('Service Tests', () => {
  describe('Item Service', () => {
    let service: ItemService;
    let httpMock: HttpTestingController;
    let elemDefault: IItem;
    let expectedResult: IItem | IItem[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ItemService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        description: 'AAAAAAA',
        category: 'AAAAAAA',
        price: 0,
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

      it('should create a Item', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Item()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Item', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            category: 'BBBBBB',
            price: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Item', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
          },
          new Item()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Item', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
            category: 'BBBBBB',
            price: 1,
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

      it('should delete a Item', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addItemToCollectionIfMissing', () => {
        it('should add a Item to an empty array', () => {
          const item: IItem = { id: 123 };
          expectedResult = service.addItemToCollectionIfMissing([], item);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(item);
        });

        it('should not add a Item to an array that contains it', () => {
          const item: IItem = { id: 123 };
          const itemCollection: IItem[] = [
            {
              ...item,
            },
            { id: 456 },
          ];
          expectedResult = service.addItemToCollectionIfMissing(itemCollection, item);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Item to an array that doesn't contain it", () => {
          const item: IItem = { id: 123 };
          const itemCollection: IItem[] = [{ id: 456 }];
          expectedResult = service.addItemToCollectionIfMissing(itemCollection, item);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(item);
        });

        it('should add only unique Item to an array', () => {
          const itemArray: IItem[] = [{ id: 123 }, { id: 456 }, { id: 73260 }];
          const itemCollection: IItem[] = [{ id: 123 }];
          expectedResult = service.addItemToCollectionIfMissing(itemCollection, ...itemArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const item: IItem = { id: 123 };
          const item2: IItem = { id: 456 };
          expectedResult = service.addItemToCollectionIfMissing([], item, item2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(item);
          expect(expectedResult).toContain(item2);
        });

        it('should accept null and undefined values', () => {
          const item: IItem = { id: 123 };
          expectedResult = service.addItemToCollectionIfMissing([], null, item, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(item);
        });

        it('should return initial array if no Item is added', () => {
          const itemCollection: IItem[] = [{ id: 123 }];
          expectedResult = service.addItemToCollectionIfMissing(itemCollection, undefined, null);
          expect(expectedResult).toEqual(itemCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
