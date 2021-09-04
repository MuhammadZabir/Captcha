jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IItemStatus, ItemStatus } from '../item-status.model';
import { ItemStatusService } from '../service/item-status.service';

import { ItemStatusRoutingResolveService } from './item-status-routing-resolve.service';

describe('Service Tests', () => {
  describe('ItemStatus routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ItemStatusRoutingResolveService;
    let service: ItemStatusService;
    let resultItemStatus: IItemStatus | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ItemStatusRoutingResolveService);
      service = TestBed.inject(ItemStatusService);
      resultItemStatus = undefined;
    });

    describe('resolve', () => {
      it('should return IItemStatus returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemStatus = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultItemStatus).toEqual({ id: 123 });
      });

      it('should return new IItemStatus if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemStatus = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultItemStatus).toEqual(new ItemStatus());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ItemStatus })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultItemStatus = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultItemStatus).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
