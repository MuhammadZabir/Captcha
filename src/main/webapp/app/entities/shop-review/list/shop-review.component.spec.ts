jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShopReviewService } from '../service/shop-review.service';

import { ShopReviewComponent } from './shop-review.component';

describe('Component Tests', () => {
  describe('ShopReview Management Component', () => {
    let comp: ShopReviewComponent;
    let fixture: ComponentFixture<ShopReviewComponent>;
    let service: ShopReviewService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ShopReviewComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ShopReviewComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ShopReviewComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ShopReviewService);

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
      expect(comp.shopReviews?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
