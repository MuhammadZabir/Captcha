import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShopReviewDetailComponent } from './shop-review-detail.component';

describe('Component Tests', () => {
  describe('ShopReview Management Detail Component', () => {
    let comp: ShopReviewDetailComponent;
    let fixture: ComponentFixture<ShopReviewDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ShopReviewDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ shopReview: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ShopReviewDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ShopReviewDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load shopReview on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.shopReview).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
