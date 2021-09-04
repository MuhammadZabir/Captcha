jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ItemStatusService } from '../service/item-status.service';
import { IItemStatus, ItemStatus } from '../item-status.model';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

import { ItemStatusUpdateComponent } from './item-status-update.component';

describe('Component Tests', () => {
  describe('ItemStatus Management Update Component', () => {
    let comp: ItemStatusUpdateComponent;
    let fixture: ComponentFixture<ItemStatusUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let itemStatusService: ItemStatusService;
    let itemService: ItemService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ItemStatusUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ItemStatusUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ItemStatusUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      itemStatusService = TestBed.inject(ItemStatusService);
      itemService = TestBed.inject(ItemService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call item query and add missing value', () => {
        const itemStatus: IItemStatus = { id: 456 };
        const item: IItem = { id: 57214 };
        itemStatus.item = item;

        const itemCollection: IItem[] = [{ id: 84095 }];
        jest.spyOn(itemService, 'query').mockReturnValue(of(new HttpResponse({ body: itemCollection })));
        const expectedCollection: IItem[] = [item, ...itemCollection];
        jest.spyOn(itemService, 'addItemToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ itemStatus });
        comp.ngOnInit();

        expect(itemService.query).toHaveBeenCalled();
        expect(itemService.addItemToCollectionIfMissing).toHaveBeenCalledWith(itemCollection, item);
        expect(comp.itemsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const itemStatus: IItemStatus = { id: 456 };
        const item: IItem = { id: 37696 };
        itemStatus.item = item;

        activatedRoute.data = of({ itemStatus });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(itemStatus));
        expect(comp.itemsCollection).toContain(item);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemStatus>>();
        const itemStatus = { id: 123 };
        jest.spyOn(itemStatusService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: itemStatus }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(itemStatusService.update).toHaveBeenCalledWith(itemStatus);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemStatus>>();
        const itemStatus = new ItemStatus();
        jest.spyOn(itemStatusService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: itemStatus }));
        saveSubject.complete();

        // THEN
        expect(itemStatusService.create).toHaveBeenCalledWith(itemStatus);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ItemStatus>>();
        const itemStatus = { id: 123 };
        jest.spyOn(itemStatusService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ itemStatus });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(itemStatusService.update).toHaveBeenCalledWith(itemStatus);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
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
