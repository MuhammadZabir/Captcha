jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ItemReviewService } from '../service/item-review.service';

import { ItemReviewComponent } from './item-review.component';

describe('Component Tests', () => {
  describe('ItemReview Management Component', () => {
    let comp: ItemReviewComponent;
    let fixture: ComponentFixture<ItemReviewComponent>;
    let service: ItemReviewService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ItemReviewComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ItemReviewComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ItemReviewComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ItemReviewService);

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
      expect(comp.itemReviews?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
