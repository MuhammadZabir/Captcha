jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartBasketService } from '../service/cart-basket.service';

import { CartBasketComponent } from './cart-basket.component';

describe('Component Tests', () => {
  describe('CartBasket Management Component', () => {
    let comp: CartBasketComponent;
    let fixture: ComponentFixture<CartBasketComponent>;
    let service: CartBasketService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartBasketComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(CartBasketComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartBasketComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CartBasketService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.cartBaskets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
