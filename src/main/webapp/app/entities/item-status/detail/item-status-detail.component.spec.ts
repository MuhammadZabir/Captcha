import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ItemStatusDetailComponent } from './item-status-detail.component';

describe('Component Tests', () => {
  describe('ItemStatus Management Detail Component', () => {
    let comp: ItemStatusDetailComponent;
    let fixture: ComponentFixture<ItemStatusDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ItemStatusDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ itemStatus: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ItemStatusDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ItemStatusDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load itemStatus on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.itemStatus).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
