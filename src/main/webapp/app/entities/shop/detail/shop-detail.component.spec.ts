import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShopDetailComponent } from './shop-detail.component';

describe('Component Tests', () => {
  describe('Shop Management Detail Component', () => {
    let comp: ShopDetailComponent;
    let fixture: ComponentFixture<ShopDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ShopDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ shop: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ShopDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ShopDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load shop on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.shop).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
