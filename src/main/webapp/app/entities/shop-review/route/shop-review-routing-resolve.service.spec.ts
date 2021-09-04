jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IShopReview, ShopReview } from '../shop-review.model';
import { ShopReviewService } from '../service/shop-review.service';

import { ShopReviewRoutingResolveService } from './shop-review-routing-resolve.service';

describe('Service Tests', () => {
  describe('ShopReview routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ShopReviewRoutingResolveService;
    let service: ShopReviewService;
    let resultShopReview: IShopReview | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ShopReviewRoutingResolveService);
      service = TestBed.inject(ShopReviewService);
      resultShopReview = undefined;
    });

    describe('resolve', () => {
      it('should return IShopReview returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShopReview = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultShopReview).toEqual({ id: 123 });
      });

      it('should return new IShopReview if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShopReview = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultShopReview).toEqual(new ShopReview());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ShopReview })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultShopReview = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultShopReview).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
