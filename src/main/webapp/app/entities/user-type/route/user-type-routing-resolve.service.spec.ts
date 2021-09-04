jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IUserType, UserType } from '../user-type.model';
import { UserTypeService } from '../service/user-type.service';

import { UserTypeRoutingResolveService } from './user-type-routing-resolve.service';

describe('Service Tests', () => {
  describe('UserType routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: UserTypeRoutingResolveService;
    let service: UserTypeService;
    let resultUserType: IUserType | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(UserTypeRoutingResolveService);
      service = TestBed.inject(UserTypeService);
      resultUserType = undefined;
    });

    describe('resolve', () => {
      it('should return IUserType returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserType = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUserType).toEqual({ id: 123 });
      });

      it('should return new IUserType if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserType = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultUserType).toEqual(new UserType());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as UserType })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserType = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUserType).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
