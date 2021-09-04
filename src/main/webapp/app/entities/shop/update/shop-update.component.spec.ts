jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ShopService } from '../service/shop.service';
import { IShop, Shop } from '../shop.model';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';

import { ShopUpdateComponent } from './shop-update.component';

describe('Component Tests', () => {
  describe('Shop Management Update Component', () => {
    let comp: ShopUpdateComponent;
    let fixture: ComponentFixture<ShopUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let shopService: ShopService;
    let userExtraService: UserExtraService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ShopUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ShopUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ShopUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      shopService = TestBed.inject(ShopService);
      userExtraService = TestBed.inject(UserExtraService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call owner query and add missing value', () => {
        const shop: IShop = { id: 456 };
        const owner: IUserExtra = { id: 27734 };
        shop.owner = owner;

        const ownerCollection: IUserExtra[] = [{ id: 28875 }];
        jest.spyOn(userExtraService, 'query').mockReturnValue(of(new HttpResponse({ body: ownerCollection })));
        const expectedCollection: IUserExtra[] = [owner, ...ownerCollection];
        jest.spyOn(userExtraService, 'addUserExtraToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ shop });
        comp.ngOnInit();

        expect(userExtraService.query).toHaveBeenCalled();
        expect(userExtraService.addUserExtraToCollectionIfMissing).toHaveBeenCalledWith(ownerCollection, owner);
        expect(comp.ownersCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const shop: IShop = { id: 456 };
        const owner: IUserExtra = { id: 30767 };
        shop.owner = owner;

        activatedRoute.data = of({ shop });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(shop));
        expect(comp.ownersCollection).toContain(owner);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shop>>();
        const shop = { id: 123 };
        jest.spyOn(shopService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shop });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shop }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(shopService.update).toHaveBeenCalledWith(shop);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shop>>();
        const shop = new Shop();
        jest.spyOn(shopService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shop });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: shop }));
        saveSubject.complete();

        // THEN
        expect(shopService.create).toHaveBeenCalledWith(shop);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Shop>>();
        const shop = { id: 123 };
        jest.spyOn(shopService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ shop });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(shopService.update).toHaveBeenCalledWith(shop);
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
    });
  });
});
