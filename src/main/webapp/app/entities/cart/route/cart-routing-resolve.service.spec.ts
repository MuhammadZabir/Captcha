jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICart, Cart } from '../cart.model';
import { CartService } from '../service/cart.service';

import { CartRoutingResolveService } from './cart-routing-resolve.service';

describe('Service Tests', () => {
  describe('Cart routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CartRoutingResolveService;
    let service: CartService;
    let resultCart: ICart | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CartRoutingResolveService);
      service = TestBed.inject(CartService);
      resultCart = undefined;
    });

    describe('resolve', () => {
      it('should return ICart returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCart = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCart).toEqual({ id: 123 });
      });

      it('should return new ICart if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCart = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCart).toEqual(new Cart());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Cart })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCart = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCart).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
