import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartDetailComponent } from './cart-detail.component';

describe('Component Tests', () => {
  describe('Cart Management Detail Component', () => {
    let comp: CartDetailComponent;
    let fixture: ComponentFixture<CartDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CartDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ cart: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CartDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CartDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load cart on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cart).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
