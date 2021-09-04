jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ShopReviewService } from '../service/shop-review.service';
import { IShopReview, ShopReview } from '../shop-review.model';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { IShop } from 'app/entities/shop/shop.model';
import { ShopService } from 'app/entities/shop/service/shop.service';

import { ShopReviewUpdateComponent } from './shop-review-update.component';

describe('Component Tests', () => {
  describe('ShopReview Management Update Component', () => {
    let comp: ShopReviewUpdateComponent;
    let fixture: ComponentFixture<ShopReviewUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let shopReviewService: ShopReviewService;
    let userExtraService: UserExtraService;
    let shopService: ShopService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ShopReviewUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ShopReviewUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ShopReviewUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      shopReviewService = TestBed.inject(ShopReviewService);
      userExtraService = TestBed.inject(UserExtraService);
      shopService = TestBed.inject(ShopService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call reviewer query and add missing value', () => {
        const shopReview: IShopReview = { id: 456 };
        const reviewer: IUserExtra = { id: 65840 };
        shopReview.reviewer = reviewer;

        const reviewerCollection: IUserExtra[] = [{ id: 47394 }];
        jest.spyOn(userExtraService, 'query').mockReturnValue(of(new HttpResponse({ body: reviewerCollection })));
        const expectedCollection: IUserExtra[] = [reviewer, ...reviewerCollection];
        jest.spyOn(userExtraService, 'addUserExtraToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        expect(userExtraService.query).toHaveBeenCalled();
        expect(userExtraService.addUserExtraToCollectionIfMissing).toHaveBeenCalledWith(reviewerCollection, reviewer);
        expect(comp.reviewersCollection).toEqual(expectedCollection);
      });

      it('Should call Shop query and add missing value', () => {
        const shopReview: IShopReview = { id: 456 };
        const shop: IShop = { id: 356 };
        shopReview.shop = shop;

        const shopCollection: IShop[] = [{ id: 20104 }];
        jest.spyOn(shopService, 'query').mockReturnValue(of(new HttpResponse({ body: shopCollection })));
        const additionalShops = [shop];
        const expectedCollection: IShop[] = [...additionalShops, ...shopCollection];
        jest.spyOn(shopService, 'addShopToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        expect(shopService.query).toHaveBeenCalled();
        expect(shopService.addShopToCollectionIfMissing).toHaveBeenCalledWith(shopCollection, ...additionalShops);
        expect(comp.shopsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const shopReview: IShopReview = { id: 456 };
        const reviewer: IUserExtra = { id: 37322 };
        shopReview.reviewer = reviewer;
        const shop: IShop = { id: 95871 };
        shopReview.shop = shop;

        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(shopReview));
        expect(comp.reviewersCollection).toContain(reviewer);
        expect(comp.shopsSharedCollection).toContain(shop);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ShopReview>>();
        const shopReview = { id: 123 };
        jest.spyOn(shopReviewService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shopReview }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(shopReviewService.update).toHaveBeenCalledWith(shopReview);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ShopReview>>();
        const shopReview = new ShopReview();
        jest.spyOn(shopReviewService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shopReview }));
        saveSubject.complete();

        // THEN
        expect(shopReviewService.create).toHaveBeenCalledWith(shopReview);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ShopReview>>();
        const shopReview = { id: 123 };
        jest.spyOn(shopReviewService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shopReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(shopReviewService.update).toHaveBeenCalledWith(shopReview);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserExtraById', () => {
        it('Should return tracked UserExtra primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserExtraById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackShopById', () => {
        it('Should return tracked Shop primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackShopById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
