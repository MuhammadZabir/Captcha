import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartBasketDetailComponent } from './cart-basket-detail.component';

describe('Component Tests', () => {
  describe('CartBasket Management Detail Component', () => {
    let comp: CartBasketDetailComponent;
    let fixture: ComponentFixture<CartBasketDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CartBasketDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ cartBasket: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CartBasketDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CartBasketDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load cartBasket on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cartBasket).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
