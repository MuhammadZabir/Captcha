jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CartBasketService } from '../service/cart-basket.service';
import { ICartBasket, CartBasket } from '../cart-basket.model';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

import { CartBasketUpdateComponent } from './cart-basket-update.component';

describe('Component Tests', () => {
  describe('CartBasket Management Update Component', () => {
    let comp: CartBasketUpdateComponent;
    let fixture: ComponentFixture<CartBasketUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let cartBasketService: CartBasketService;
    let cartService: CartService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartBasketUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CartBasketUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartBasketUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      cartBasketService = TestBed.inject(CartBasketService);
      cartService = TestBed.inject(CartService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Cart query and add missing value', () => {
        const cartBasket: ICartBasket = { id: 456 };
        const cart: ICart = { id: 45971 };
        cartBasket.cart = cart;

        const cartCollection: ICart[] = [{ id: 78442 }];
        jest.spyOn(cartService, 'query').mockReturnValue(of(new HttpResponse({ body: cartCollection })));
        const additionalCarts = [cart];
        const expectedCollection: ICart[] = [...additionalCarts, ...cartCollection];
        jest.spyOn(cartService, 'addCartToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ cartBasket });
        comp.ngOnInit();

        expect(cartService.query).toHaveBeenCalled();
        expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, ...additionalCarts);
        expect(comp.cartsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const cartBasket: ICartBasket = { id: 456 };
        const cart: ICart = { id: 83012 };
        cartBasket.cart = cart;

        activatedRoute.data = of({ cartBasket });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(cartBasket));
        expect(comp.cartsSharedCollection).toContain(cart);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<CartBasket>>();
        const cartBasket = { id: 123 };
        jest.spyOn(cartBasketService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartBasket });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartBasket }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(cartBasketService.update).toHaveBeenCalledWith(cartBasket);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<CartBasket>>();
        const cartBasket = new CartBasket();
        jest.spyOn(cartBasketService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartBasket });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: cartBasket }));
        saveSubject.complete();

        // THEN
        expect(cartBasketService.create).toHaveBeenCalledWith(cartBasket);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<CartBasket>>();
        const cartBasket = { id: 123 };
        jest.spyOn(cartBasketService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ cartBasket });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(cartBasketService.update).toHaveBeenCalledWith(cartBasket);
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
    });
  });
});
