jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PurchaseHistoryService } from '../service/purchase-history.service';
import { IPurchaseHistory, PurchaseHistory } from '../purchase-history.model';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';

import { PurchaseHistoryUpdateComponent } from './purchase-history-update.component';

describe('Component Tests', () => {
  describe('PurchaseHistory Management Update Component', () => {
    let comp: PurchaseHistoryUpdateComponent;
    let fixture: ComponentFixture<PurchaseHistoryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let purchaseHistoryService: PurchaseHistoryService;
    let cartService: CartService;
    let userExtraService: UserExtraService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PurchaseHistoryUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PurchaseHistoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PurchaseHistoryUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      purchaseHistoryService = TestBed.inject(PurchaseHistoryService);
      cartService = TestBed.inject(CartService);
      userExtraService = TestBed.inject(UserExtraService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call cart query and add missing value', () => {
        const purchaseHistory: IPurchaseHistory = { id: 456 };
        const cart: ICart = { id: 46974 };
        purchaseHistory.cart = cart;

        const cartCollection: ICart[] = [{ id: 2499 }];
        jest.spyOn(cartService, 'query').mockReturnValue(of(new HttpResponse({ body: cartCollection })));
        const expectedCollection: ICart[] = [cart, ...cartCollection];
        jest.spyOn(cartService, 'addCartToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        expect(cartService.query).toHaveBeenCalled();
        expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, cart);
        expect(comp.cartsCollection).toEqual(expectedCollection);
      });

      it('Should call UserExtra query and add missing value', () => {
        const purchaseHistory: IPurchaseHistory = { id: 456 };
        const buyer: IUserExtra = { id: 29793 };
        purchaseHistory.buyer = buyer;

        const userExtraCollection: IUserExtra[] = [{ id: 82385 }];
        jest.spyOn(userExtraService, 'query').mockReturnValue(of(new HttpResponse({ body: userExtraCollection })));
        const additionalUserExtras = [buyer];
        const expectedCollection: IUserExtra[] = [...additionalUserExtras, ...userExtraCollection];
        jest.spyOn(userExtraService, 'addUserExtraToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        expect(userExtraService.query).toHaveBeenCalled();
        expect(userExtraService.addUserExtraToCollectionIfMissing).toHaveBeenCalledWith(userExtraCollection, ...additionalUserExtras);
        expect(comp.userExtrasSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const purchaseHistory: IPurchaseHistory = { id: 456 };
        const cart: ICart = { id: 12985 };
        purchaseHistory.cart = cart;
        const buyer: IUserExtra = { id: 5614 };
        purchaseHistory.buyer = buyer;

        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(purchaseHistory));
        expect(comp.cartsCollection).toContain(cart);
        expect(comp.userExtrasSharedCollection).toContain(buyer);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<PurchaseHistory>>();
        const purchaseHistory = { id: 123 };
        jest.spyOn(purchaseHistoryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: purchaseHistory }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(purchaseHistoryService.update).toHaveBeenCalledWith(purchaseHistory);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<PurchaseHistory>>();
        const purchaseHistory = new PurchaseHistory();
        jest.spyOn(purchaseHistoryService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: purchaseHistory }));
        saveSubject.complete();

        // THEN
        expect(purchaseHistoryService.create).toHaveBeenCalledWith(purchaseHistory);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<PurchaseHistory>>();
        const purchaseHistory = { id: 123 };
        jest.spyOn(purchaseHistoryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ purchaseHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(purchaseHistoryService.update).toHaveBeenCalledWith(purchaseHistory);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCartById', () => {
        it('Should return tracked Cart primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCartById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

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
