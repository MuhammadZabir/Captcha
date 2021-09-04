import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PurchaseHistoryDetailComponent } from './purchase-history-detail.component';

describe('Component Tests', () => {
  describe('PurchaseHistory Management Detail Component', () => {
    let comp: PurchaseHistoryDetailComponent;
    let fixture: ComponentFixture<PurchaseHistoryDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PurchaseHistoryDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ purchaseHistory: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PurchaseHistoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PurchaseHistoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load purchaseHistory on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.purchaseHistory).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
