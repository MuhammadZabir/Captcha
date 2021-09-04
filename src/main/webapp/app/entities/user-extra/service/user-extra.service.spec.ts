import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserExtra, UserExtra } from '../user-extra.model';

import { UserExtraService } from './user-extra.service';

describe('Service Tests', () => {
  describe('UserExtra Service', () => {
    let service: UserExtraService;
    let httpMock: HttpTestingController;
    let elemDefault: IUserExtra;
    let expectedResult: IUserExtra | IUserExtra[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(UserExtraService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        billingAddress: 'AAAAAAA',
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

      it('should create a UserExtra', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new UserExtra()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a UserExtra', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            billingAddress: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a UserExtra', () => {
        const patchObject = Object.assign(
          {
            billingAddress: 'BBBBBB',
          },
          new UserExtra()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of UserExtra', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            billingAddress: 'BBBBBB',
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

      it('should delete a UserExtra', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addUserExtraToCollectionIfMissing', () => {
        it('should add a UserExtra to an empty array', () => {
          const userExtra: IUserExtra = { id: 123 };
          expectedResult = service.addUserExtraToCollectionIfMissing([], userExtra);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(userExtra);
        });

        it('should not add a UserExtra to an array that contains it', () => {
          const userExtra: IUserExtra = { id: 123 };
          const userExtraCollection: IUserExtra[] = [
            {
              ...userExtra,
            },
            { id: 456 },
          ];
          expectedResult = service.addUserExtraToCollectionIfMissing(userExtraCollection, userExtra);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a UserExtra to an array that doesn't contain it", () => {
          const userExtra: IUserExtra = { id: 123 };
          const userExtraCollection: IUserExtra[] = [{ id: 456 }];
          expectedResult = service.addUserExtraToCollectionIfMissing(userExtraCollection, userExtra);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(userExtra);
        });

        it('should add only unique UserExtra to an array', () => {
          const userExtraArray: IUserExtra[] = [{ id: 123 }, { id: 456 }, { id: 81333 }];
          const userExtraCollection: IUserExtra[] = [{ id: 123 }];
          expectedResult = service.addUserExtraToCollectionIfMissing(userExtraCollection, ...userExtraArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const userExtra: IUserExtra = { id: 123 };
          const userExtra2: IUserExtra = { id: 456 };
          expectedResult = service.addUserExtraToCollectionIfMissing([], userExtra, userExtra2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(userExtra);
          expect(expectedResult).toContain(userExtra2);
        });

        it('should accept null and undefined values', () => {
          const userExtra: IUserExtra = { id: 123 };
          expectedResult = service.addUserExtraToCollectionIfMissing([], null, userExtra, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(userExtra);
        });

        it('should return initial array if no UserExtra is added', () => {
          const userExtraCollection: IUserExtra[] = [{ id: 123 }];
          expectedResult = service.addUserExtraToCollectionIfMissing(userExtraCollection, undefined, null);
          expect(expectedResult).toEqual(userExtraCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
