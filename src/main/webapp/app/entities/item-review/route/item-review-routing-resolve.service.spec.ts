jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IItemReview, ItemReview } from '../item-review.model';
import { ItemReviewService } from '../service/item-review.service';

import { ItemReviewRoutingResolveService } from './item-review-routing-resolve.service';

describe('Service Tests', () => {
  describe('ItemReview routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ItemReviewRoutingResolveService;
    let service: ItemReviewService;
    let resultItemReview: IItemReview | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ItemReviewRoutingResolveService);
      service = TestBed.inject(ItemReviewService);
      resultItemReview = undefined;
    });

    describe('resolve', () => {
      it('should return IItemReview returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemReview = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultItemReview).toEqual({ id: 123 });
      });

      it('should return new IItemReview if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemReview = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultItemReview).toEqual(new ItemReview());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ItemReview })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemReview = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultItemReview).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
