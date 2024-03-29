jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CartService } from '../service/cart.service';

import { CartComponent } from './cart.component';

describe('Component Tests', () => {
  describe('Cart Management Component', () => {
    let comp: CartComponent;
    let fixture: ComponentFixture<CartComponent>;
    let service: CartService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CartComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(CartComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CartComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CartService);

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
      expect(comp.carts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
