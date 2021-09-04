jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PurchaseHistoryService } from '../service/purchase-history.service';

import { PurchaseHistoryComponent } from './purchase-history.component';

describe('Component Tests', () => {
  describe('PurchaseHistory Management Component', () => {
    let comp: PurchaseHistoryComponent;
    let fixture: ComponentFixture<PurchaseHistoryComponent>;
    let service: PurchaseHistoryService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PurchaseHistoryComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(PurchaseHistoryComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PurchaseHistoryComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PurchaseHistoryService);

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
      expect(comp.purchaseHistories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
