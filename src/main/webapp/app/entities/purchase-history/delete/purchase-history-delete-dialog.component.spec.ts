jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PurchaseHistoryService } from '../service/purchase-history.service';

import { PurchaseHistoryDeleteDialogComponent } from './purchase-history-delete-dialog.component';

describe('Component Tests', () => {
  describe('PurchaseHistory Management Delete Component', () => {
    let comp: PurchaseHistoryDeleteDialogComponent;
    let fixture: ComponentFixture<PurchaseHistoryDeleteDialogComponent>;
    let service: PurchaseHistoryService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PurchaseHistoryDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(PurchaseHistoryDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PurchaseHistoryDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PurchaseHistoryService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({})));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        jest.spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
