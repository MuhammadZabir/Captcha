jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ImageService } from '../service/image.service';
import { IImage, Image } from '../image.model';
import { IItem } from 'app/entities/item/item.model';
import { ItemService } from 'app/entities/item/service/item.service';

import { ImageUpdateComponent } from './image-update.component';

describe('Component Tests', () => {
  describe('Image Management Update Component', () => {
    let comp: ImageUpdateComponent;
    let fixture: ComponentFixture<ImageUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let imageService: ImageService;
    let itemService: ItemService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ImageUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ImageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ImageUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      imageService = TestBed.inject(ImageService);
      itemService = TestBed.inject(ItemService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Item query and add missing value', () => {
        const image: IImage = { id: 456 };
        const item: IItem = { id: 68376 };
        image.item = item;

        const itemCollection: IItem[] = [{ id: 74732 }];
        jest.spyOn(itemService, 'query').mockReturnValue(of(new HttpResponse({ body: itemCollection })));
        const additionalItems = [item];
        const expectedCollection: IItem[] = [...additionalItems, ...itemCollection];
        jest.spyOn(itemService, 'addItemToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ image });
        comp.ngOnInit();

        expect(itemService.query).toHaveBeenCalled();
        expect(itemService.addItemToCollectionIfMissing).toHaveBeenCalledWith(itemCollection, ...additionalItems);
        expect(comp.itemsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const image: IImage = { id: 456 };
        const item: IItem = { id: 49482 };
        image.item = item;

        activatedRoute.data = of({ image });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(image));
        expect(comp.itemsSharedCollection).toContain(item);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Image>>();
        const image = { id: 123 };
        jest.spyOn(imageService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ image });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: image }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(imageService.update).toHaveBeenCalledWith(image);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Image>>();
        const image = new Image();
        jest.spyOn(imageService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ image });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: image }));
        saveSubject.complete();

        // THEN
        expect(imageService.create).toHaveBeenCalledWith(image);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Image>>();
        const image = { id: 123 };
        jest.spyOn(imageService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ image });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(imageService.update).toHaveBeenCalledWith(image);
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
