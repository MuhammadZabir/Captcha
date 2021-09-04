import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserType, UserType } from '../user-type.model';

import { UserTypeService } from './user-type.service';

describe('Service Tests', () => {
  describe('UserType Service', () => {
    let service: UserTypeService;
    let httpMock: HttpTestingController;
    let elemDefault: IUserType;
    let expectedResult: IUserType | IUserType[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(UserTypeService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        description: 'AAAAAAA',
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

      it('should create a UserType', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new UserType()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a UserType', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a UserType', () => {
        const patchObject = Object.assign(
          {
            description: 'BBBBBB',
          },
          new UserType()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of UserType', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            description: 'BBBBBB',
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

      it('should delete a UserType', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addUserTypeToCollectionIfMissing', () => {
        it('should add a UserType to an empty array', () => {
          const userType: IUserType = { id: 123 };
          expectedResult = service.addUserTypeToCollectionIfMissing([], userType);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(userType);
        });

        it('should not add a UserType to an array that contains it', () => {
          const userType: IUserType = { id: 123 };
          const userTypeCollection: IUserType[] = [
            {
              ...userType,
            },
            { id: 456 },
          ];
          expectedResult = service.addUserTypeToCollectionIfMissing(userTypeCollection, userType);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a UserType to an array that doesn't contain it", () => {
          const userType: IUserType = { id: 123 };
          const userTypeCollection: IUserType[] = [{ id: 456 }];
          expectedResult = service.addUserTypeToCollectionIfMissing(userTypeCollection, userType);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(userType);
        });

        it('should add only unique UserType to an array', () => {
          const userTypeArray: IUserType[] = [{ id: 123 }, { id: 456 }, { id: 45876 }];
          const userTypeCollection: IUserType[] = [{ id: 123 }];
          expectedResult = service.addUserTypeToCollectionIfMissing(userTypeCollection, ...userTypeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const userType: IUserType = { id: 123 };
          const userType2: IUserType = { id: 456 };
          expectedResult = service.addUserTypeToCollectionIfMissing([], userType, userType2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(userType);
          expect(expectedResult).toContain(userType2);
        });

        it('should accept null and undefined values', () => {
          const userType: IUserType = { id: 123 };
          expectedResult = service.addUserTypeToCollectionIfMissing([], null, userType, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(userType);
        });

        it('should return initial array if no UserType is added', () => {
          const userTypeCollection: IUserType[] = [{ id: 123 }];
          expectedResult = service.addUserTypeToCollectionIfMissing(userTypeCollection, undefined, null);
          expect(expectedResult).toEqual(userTypeCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
