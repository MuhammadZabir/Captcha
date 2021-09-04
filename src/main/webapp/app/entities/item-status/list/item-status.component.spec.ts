jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ItemStatusService } from '../service/item-status.service';

import { ItemStatusComponent } from './item-status.component';

describe('Component Tests', () => {
  describe('ItemStatus Management Component', () => {
    let comp: ItemStatusComponent;
    let fixture: ComponentFixture<ItemStatusComponent>;
    let service: ItemStatusService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ItemStatusComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ItemStatusComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ItemStatusComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ItemStatusService);

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
      expect(comp.itemStatuses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
