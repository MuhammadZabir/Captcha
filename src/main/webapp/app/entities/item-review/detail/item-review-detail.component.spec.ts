import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ItemReviewDetailComponent } from './item-review-detail.component';

describe('Component Tests', () => {
  describe('ItemReview Management Detail Component', () => {
    let comp: ItemReviewDetailComponent;
    let fixture: ComponentFixture<ItemReviewDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ItemReviewDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ itemReview: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ItemReviewDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ItemReviewDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load itemReview on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.itemReview).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
