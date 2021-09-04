jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ItemReviewService } from '../service/item-review.service';
import { IItemReview, ItemReview } from '../item-review.model';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

import { ItemReviewUpdateComponent } from './item-review-update.component';

describe('Component Tests', () => {
  describe('ItemReview Management Update Component', () => {
    let comp: ItemReviewUpdateComponent;
    let fixture: ComponentFixture<ItemReviewUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let itemReviewService: ItemReviewService;
    let userExtraService: UserExtraService;
    let itemService: ItemService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ItemReviewUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ItemReviewUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ItemReviewUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      itemReviewService = TestBed.inject(ItemReviewService);
      userExtraService = TestBed.inject(UserExtraService);
      itemService = TestBed.inject(ItemService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call reviewer query and add missing value', () => {
        const itemReview: IItemReview = { id: 456 };
        const reviewer: IUserExtra = { id: 43867 };
        itemReview.reviewer = reviewer;

        const reviewerCollection: IUserExtra[] = [{ id: 48149 }];
        jest.spyOn(userExtraService, 'query').mockReturnValue(of(new HttpResponse({ body: reviewerCollection })));
        const expectedCollection: IUserExtra[] = [reviewer, ...reviewerCollection];
        jest.spyOn(userExtraService, 'addUserExtraToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        expect(userExtraService.query).toHaveBeenCalled();
        expect(userExtraService.addUserExtraToCollectionIfMissing).toHaveBeenCalledWith(reviewerCollection, reviewer);
        expect(comp.reviewersCollection).toEqual(expectedCollection);
      });

      it('Should call Item query and add missing value', () => {
        const itemReview: IItemReview = { id: 456 };
        const item: IItem = { id: 70702 };
        itemReview.item = item;

        const itemCollection: IItem[] = [{ id: 85291 }];
        jest.spyOn(itemService, 'query').mockReturnValue(of(new HttpResponse({ body: itemCollection })));
        const additionalItems = [item];
        const expectedCollection: IItem[] = [...additionalItems, ...itemCollection];
        jest.spyOn(itemService, 'addItemToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        expect(itemService.query).toHaveBeenCalled();
        expect(itemService.addItemToCollectionIfMissing).toHaveBeenCalledWith(itemCollection, ...additionalItems);
        expect(comp.itemsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const itemReview: IItemReview = { id: 456 };
        const reviewer: IUserExtra = { id: 28235 };
        itemReview.reviewer = reviewer;
        const item: IItem = { id: 42562 };
        itemReview.item = item;

        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(itemReview));
        expect(comp.reviewersCollection).toContain(reviewer);
        expect(comp.itemsSharedCollection).toContain(item);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemReview>>();
        const itemReview = { id: 123 };
        jest.spyOn(itemReviewService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: itemReview }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(itemReviewService.update).toHaveBeenCalledWith(itemReview);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemReview>>();
        const itemReview = new ItemReview();
        jest.spyOn(itemReviewService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: itemReview }));
        saveSubject.complete();

        // THEN
        expect(itemReviewService.create).toHaveBeenCalledWith(itemReview);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemReview>>();
        const itemReview = { id: 123 };
        jest.spyOn(itemReviewService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemReview });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(itemReviewService.update).toHaveBeenCalledWith(itemReview);
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

      describe('trackItemById', () => {
        it('Should return tracked Item primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackItemById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
